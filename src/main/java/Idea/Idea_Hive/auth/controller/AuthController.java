package Idea.Idea_Hive.auth.controller;

import Idea.Idea_Hive.auth.dto.request.EmailLoginRequest;
import Idea.Idea_Hive.auth.dto.response.AuthInfoResponse;
import Idea.Idea_Hive.auth.dto.response.LoginResponse;
import Idea.Idea_Hive.auth.dto.response.TokenResponse;
import Idea.Idea_Hive.auth.service.AuthService;
import Idea.Idea_Hive.auth.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    // application.yml 또는 application-{profile}.yml 에 정의된 값 주입
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.sameSite}")
    private String cookieSameSite; // "Lax", "Strict", "None"

    @Value("${app.cookie.http-only}")
    private boolean cookieHttpOnly;

    @Value("${app.cookie.path}")
    private String cookiePath;

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

//        // 리프레시 토큰을 HttpOnly 쿠키로 설정
//        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.refreshToken());
//        refreshTokenCookie.setHttpOnly(cookieHttpOnly); // JavaScript에서 접근 불가
//        refreshTokenCookie.setSecure(cookieSecure);   // HTTPS에서만 전송
//        refreshTokenCookie.setPath("/");      // 전체 경로에 대해 유효
//        refreshTokenCookie.setMaxAge((int) (tokenService.getRefreshTokenValidityInMilliseconds() / 1000)); // 만료 시간 설정

        // 리프레시 토큰을 HttpOnly 쿠키로 설정 (ResponseCookie 사용)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)   // 프로파일에 따라 동적으로 설정
                .path(cookiePath)
                .maxAge(tokenService.getRefreshTokenValidityInMilliseconds() / 1000)
                .sameSite(cookieSameSite) // 프로파일에 따라 동적으로 설정
                // .domain(cookieDomain) // 필요시 도메인 설정
                .build();

//        response.addCookie(refreshTokenCookie);
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        LoginResponse loginResponse = new LoginResponse(
                authInfoResponse.email(),
                authInfoResponse.name(),
                tokens.accessToken()
        );

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request,
                                         HttpServletResponse response) {
        Optional<String> refreshTokenOpt = extractTokenFromCookie(request, "refreshToken");

        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token not found.");
        }

        String refreshToken = refreshTokenOpt.get();
        String email = tokenService.getEmail(refreshToken);
        tokenService.deleteRefreshToken(email);

        // 쿠키 만료시키기
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)   // 프로파일에 따라 동적으로 설정
                .path(cookiePath)
                .maxAge(tokenService.getRefreshTokenValidityInMilliseconds() / 1000)
                .sameSite(cookieSameSite) // 프로파일에 따라 동적으로 설정
                // .domain(cookieDomain) // 필요시 도메인 설정
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok("로그아웃 완료");
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
//        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.refreshToken());
//        refreshTokenCookie.setHttpOnly(cookieHttpOnly); // JavaScript에서 접근 불가
//        refreshTokenCookie.setSecure(cookieSecure);   // HTTPS에서만 전송
//        refreshTokenCookie.setPath("/");      // 전체 경로에 대해 유효
//        refreshTokenCookie.setMaxAge((int) (tokenService.getRefreshTokenValidityInMilliseconds() / 1000)); // 만료 시간 설정
//
//        response.addCookie(refreshTokenCookie);// 갱신된 쿠키 설정

        // 리프레시 토큰을 HttpOnly 쿠키로 설정 (ResponseCookie 사용)
        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken())
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)   // 프로파일에 따라 동적으로 설정
                .path(cookiePath)
                .maxAge(tokenService.getRefreshTokenValidityInMilliseconds() / 1000)
                .sameSite(cookieSameSite) // 프로파일에 따라 동적으로 설정
                // .domain(cookieDomain)
                .build();

        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());

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