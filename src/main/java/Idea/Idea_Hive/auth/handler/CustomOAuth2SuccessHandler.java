package Idea.Idea_Hive.auth.handler;

import Idea.Idea_Hive.auth.service.TokenService;
import Idea.Idea_Hive.common.constant.Constants;
import Idea.Idea_Hive.member.entity.dto.response.SignUpResponse;
import Idea.Idea_Hive.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 로그인 성공 이후의 후처리 → JWT 발급, 리다이렉트 등의 책임을 가짐
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final TokenService tokenService;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        SignUpResponse signUpResponse;
        try {
            signUpResponse = memberService.handleOAuth2User(attributes);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.setContentType("application/json;charset=UTF-8");

            String jsonError = String.format("{\"error\": \"%s\"}", e.getMessage());
            response.getWriter().write(jsonError);
            return;
        }


        // todo: 임시 RefreshToken 발급
        String refreshToken = tokenService.createTempRefreshToken(signUpResponse.email());

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가
        refreshTokenCookie.setSecure(false);   // HTTPS에서만 전송
        refreshTokenCookie.setPath("/");      // 전체 경로에 대해 유효
        refreshTokenCookie.setMaxAge((int) (tokenService.getRefreshTokenValidityInMilliseconds() / 1000)); // 만료 시간 설정

        response.addCookie(refreshTokenCookie);

        // todo: 운영 시 url 수정 필요 ..
        String url = FRONTEND_URL + "/auth/social";
        response.sendRedirect(url);
    }
}
