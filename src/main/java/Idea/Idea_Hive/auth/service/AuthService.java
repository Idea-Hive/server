package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.auth.dto.request.EmailLoginRequest;
import Idea.Idea_Hive.auth.dto.response.AuthInfoResponse;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberJpaRepo memberJpaRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthInfoResponse getAuthInfo(@Valid EmailLoginRequest request) {
        Member member = memberJpaRepo.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일 또는 비밀번호입니다."));

        if (!passwordEncoder.matches(request.rawPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호입니다.");
        }

        return new AuthInfoResponse(member.getEmail(), member.getName());
    }
}
