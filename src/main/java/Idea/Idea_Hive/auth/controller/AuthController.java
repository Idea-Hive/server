package Idea.Idea_Hive.auth.controller;

import Idea.Idea_Hive.auth.dto.request.EmailLoginRequest;
import Idea.Idea_Hive.auth.dto.response.AuthInfoResponse;
import Idea.Idea_Hive.auth.dto.response.LoginResponse;
import Idea.Idea_Hive.auth.dto.response.TokenResponse;
import Idea.Idea_Hive.auth.service.AuthService;
import Idea.Idea_Hive.auth.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @GetMapping("/secure")
    public String secure(@AuthenticationPrincipal OAuth2User principal) {
        return "로그인한 사용자: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Email 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> emailLoginController(
            @RequestBody EmailLoginRequest request,
            HttpServletResponse response) {

        AuthInfoResponse authInfoResponse = authService.getAuthInfo(request);
        TokenResponse tokens = tokenService.createTokens(request.email());

        // 리프레시 토큰을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.refreshToken());
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가
        refreshTokenCookie.setSecure(false);   // HTTPS에서만 전송
        refreshTokenCookie.setPath("/");      // 전체 경로에 대해 유효
        refreshTokenCookie.setMaxAge((int) (tokenService.getRefreshTokenValidityInMilliseconds() / 1000)); // 만료 시간 설정

        response.addCookie(refreshTokenCookie);

        LoginResponse loginResponse = new LoginResponse(
                authInfoResponse.email(),
                authInfoResponse.name(),
                tokens.accessToken()
        );

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = extractTokenFromCookie(request, "refreshToken");

        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token not found.");
        }

        String refreshToken = refreshTokenOpt.get();

        tokenService.validateToken(refreshToken);

        String email = tokenService.getEmail(refreshToken);

        // Redis나 DB에 저장된 RefreshToken과 비교 (리프레시 토큰 재사용 방지)
        if (!tokenService.isRefreshTokenValid(email, refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unregistered Refresh Token.");
        }

        // 토큰 재발급
        TokenResponse tokenResponse = tokenService.createTokens(email);
        // 리프레시 토큰을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.refreshToken());
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가
        refreshTokenCookie.setSecure(false);   // HTTPS에서만 전송
        refreshTokenCookie.setPath("/");      // 전체 경로에 대해 유효
        refreshTokenCookie.setMaxAge((int) (tokenService.getRefreshTokenValidityInMilliseconds() / 1000)); // 만료 시간 설정

        response.addCookie(refreshTokenCookie);// 갱신된 쿠키 설정
        return ResponseEntity.ok(Map.of("accessToken", tokenResponse.accessToken()));

    }

    @GetMapping("/authtest")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("토큰 인증 성공");
    }

    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }


}