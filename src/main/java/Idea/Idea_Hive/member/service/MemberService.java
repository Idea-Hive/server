package Idea.Idea_Hive.member.service;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.hashtag.entity.repository.HashtagJpaRepo;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberJpaRepo memberJpaRepo;
    private final HashtagJpaRepo hashtagJpaRepo;
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
        log.warn("Member Entity 수정 반영해야함.");
        Member member = Member.builder()
                .email(request.email())
                .password(hashedPassword)
                .name(request.name())
                .job(request.job())
                .career(request.career())
                .type(request.type())
                .build();

        List<Hashtag> hashtags = hashtagJpaRepo.findAllById(request.hashtagIds());
        for (Hashtag hashtag : hashtags) {
            member.addHashtag(hashtag);
        }

        Member saved = memberJpaRepo.save(member);

        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

//    @Transactional
//    public SignUpResponse signUpWithSocialLogin(String email) {
//
//    }

}
