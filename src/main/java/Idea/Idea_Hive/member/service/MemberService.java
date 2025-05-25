package Idea.Idea_Hive.member.service;

import Idea.Idea_Hive.member.entity.dto.request.PasswordResetRequest;
import Idea.Idea_Hive.member.entity.dto.response.MemberInfoResponse;
import Idea.Idea_Hive.redis.RedisDao;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.skillstack.entity.repository.SkillStackJpaRepo;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.entity.dto.response.SignUpResponse;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberJpaRepo memberJpaRepo;
    private final SkillStackJpaRepo skillStackJpaRepo;
    private final PasswordEncoder passwordEncoder;
    private final RedisDao redisDao;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) throws IllegalArgumentException {
        // todo: 가입 여부 체크
        if (memberJpaRepo.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // todo: 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.password());

        // todo: DB 저장, Member Entity 변수 생성 시 추가
        Member member = Member.builder()
                .email(request.email())
                .password(hashedPassword)
                .type("email")
                .build();

        Member saved = memberJpaRepo.save(member);

        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    @Transactional
    public SignUpResponse handleOAuth2User(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String provider = (String) attributes.get("authorizedClientRegistrationId"); // google, github, kakao

        // 1. 기존 유저 존재 여부 확인
        Optional<Member> existing = memberJpaRepo.findByEmail(email);
        if (existing.isPresent()) {
            Member member = existing.get();

            // 이미 해당 소셜 로그인 계정이 존재하는 경우
            if (member.getType().equals(provider)) {
                return SignUpResponse.from(member);
            } else {
                throw new IllegalArgumentException("이미 해당 이메일로 가입된 계정이 있습니다.");
            }
        }

        // 2. 신규 회원가입 처리
        Member member = createMemberFromOAuthAttributes(attributes, provider);
        memberJpaRepo.save(member);

        return SignUpResponse.from(member);
    }

    private Member createMemberFromOAuthAttributes(Map<String, Object> attributes, String provider) {
        String email = (String) attributes.get("email");
        String name = switch (provider) {
            case "kakao" -> {
                Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile");
                yield (String) profile.get("nickname");
            }
            case "google", "github" -> (String) attributes.get("name");
            default -> "UnknownUser";
        };

        Member member = Member.builder()
                .email(email)
                .name(name)
                .type(provider)
                .build();

        return member;
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {

        Member member = memberJpaRepo.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (passwordEncoder.matches(request.newPassword(), member.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호와 일치합니다. 다른 비밀번호로 설정해주세요.");
        }

        // todo: 비밀번호 유효성 검사 추가
        validatePassword(request.newPassword());

        // todo: 비밀번호 새로 저장
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
        memberJpaRepo.save(member);

        // todo: 수정 완료 시 RefreshToken Redis에서 제거, 프론트에서도 AccessToken 제거해줘야함.
        redisDao.deleteValues(request.email()); // Refresh Token 제거
    }


    private void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하이어야 합니다.");
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("비밀번호는 영문자와 숫자를 포함해야 합니다.");
        }
    }

    public MemberInfoResponse getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 정보가 없는 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("잘못된 접근입니다.");
        }

        String userEmail = authentication.getName();
        Member member = memberJpaRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));


        return MemberInfoResponse.from(member);
    }

}
