package Idea.Idea_Hive.auth.handler;

import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그인 성공 이후의 후처리 → JWT 발급, 리다이렉트 등의 책임을 가짐
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberJpaRepo memberJpaRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1. 인증된 사용자 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // todo: 이메일 없는 경우 회원가입 페이지로 리다이렉트, 임시
        if (email == null || !memberJpaRepo.existsByEmail(email)) {
            response.sendRedirect("http://localhost:8080/api/signup/temp");
            return;
        }

        // 2. JWT 발급


        // 3. 프론트로 리다이렉팅 (토큰 담아서..)
        response.sendRedirect("http://localhost:8080/api/login/success");
    }
}
