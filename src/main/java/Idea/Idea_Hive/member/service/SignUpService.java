package Idea.Idea_Hive.member.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpService {

    private final MemberJpaRepo memberJpaRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequest request) throws IllegalArgumentException {
        // todo: 가입 여부 체크
        Boolean isExists = memberJpaRepo.existsByEmail(request.email());
        if (isExists) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // todo: 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.password());

        // todo: DB 저장, Member Entity 변수 생성 시 추가
        log.warn("Member Entity 수정 반영해야함.");
        Member member = Member.builder()
                .email(request.email())
                .name(request.name())
                .password(hashedPassword)
                .build();

        memberJpaRepo.save(member);
    }

}
