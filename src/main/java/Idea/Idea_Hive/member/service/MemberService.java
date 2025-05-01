package Idea.Idea_Hive.member.service;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.skillstack.entity.repository.SkillStackJpaRepo;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.entity.dto.response.SignUpResponse;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public boolean existsByEmail(String email) {
        return memberJpaRepo.existsByEmail(email);
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) throws IllegalArgumentException {
        // todo: 비밀번호 확인
        if (!request.password().equals(request.passwordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

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
                .name(request.name())
                .job(request.job())
                .career(request.career())
                .type(request.type())
                .build();

        List<SkillStack> skillStacks = skillStackJpaRepo.findAllById(request.skillstackIds());
        for (SkillStack skillStack : skillStacks) {
            member.addSkillStack(skillStack);
        }

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
            // 이미 존재하는 유저 → 바로 response 생성
            return SignUpResponse.from(member);
        }

        // 2. 신규 회원가입 처리
        Member member = createMemberFromOAuthAttributes(attributes, provider);
//        memberJpaRepo.save(member);

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

}
