package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.auth.dto.request.EmailLoginRequest;
import Idea.Idea_Hive.auth.dto.response.AuthInfoResponse;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthInfoResponse getAuthInfo(@Valid EmailLoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일 또는 비밀번호입니다."));

        if (!passwordEncoder.matches(request.rawPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호입니다.");
        }

        return new AuthInfoResponse(member.getEmail(), member.getName());
    }

    public boolean verifyMemberIdIsLogined(Long memberId) {
        // memberId가 현재 로그인한 멤버의 memberId인지 확인하는 메서드
        // todo: Token에 id값 추가 시 변경해야함..
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalAccessError::new);

        return member.getEmail().equals(email);
    }
}
