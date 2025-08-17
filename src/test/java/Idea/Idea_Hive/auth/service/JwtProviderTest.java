package Idea.Idea_Hive.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secretKey = "testsecretkeytestsecretkeytestsecretkeytestsecretkey";
        long accessTokenValidity = 1000L;
        long refreshTokenValidity = 2000L;
        jwtProvider = new JwtProvider(secretKey, accessTokenValidity, refreshTokenValidity);
    }

    @Test
    @DisplayName("액세스 토큰을 성공적으로 생성합니다.")
    void createAccessToken() {
        // given
        String email = "test@test.com";
        Long id = 1L;

        // when
        String accessToken = jwtProvider.createAccessToken(email, id);

        // then
        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("리프레시 토큰을 성공적으로 생성합니다.")
    void createRefreshToken() {
        // given
        String email = "test@test.com";
        Long id = 1L;

        // when
        String refreshToken = jwtProvider.createRefreshToken(email, id);

        // then
        assertThat(refreshToken).isNotNull();
    }

    @Test
    @DisplayName("유효한 토큰을 성공적으로 검증합니다.")
    void validateToken() {
        // given
        String email = "test@test.com";
        Long id = 1L;
        String token = jwtProvider.createAccessToken(email, id);

        // when & then
        jwtProvider.validateToken(token);
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 예외가 발생합니다.")
    void validateExpiredToken() throws InterruptedException {
        // given
        String email = "test@test.com";
        Long id = 1L;
        JwtProvider expiredJwtProvider = new JwtProvider("testsecretkeytestsecretkeytestsecretkeytestsecretkey", 0L, 0L);
        String token = expiredJwtProvider.createAccessToken(email, id);

        // when & then
        Thread.sleep(10);
        assertThatThrownBy(() -> expiredJwtProvider.validateToken(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("만료된 토큰입니다.");
    }

    @Test
    @DisplayName("잘못된 토큰 검증 시 예외가 발생합니다.")
    void validateInvalidToken() {
        // given
        String invalidToken = "invalidtoken";

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(invalidToken))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("잘못된 토큰입니다.");
    }

    @Test
    @DisplayName("토큰에서 이메일을 성공적으로 추출합니다.")
    void getEmail() {
        // given
        String email = "test@test.com";
        Long id = 1L;
        String token = jwtProvider.createAccessToken(email, id);

        // when
        String extractedEmail = jwtProvider.getEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("토큰에서 ID를 성공적으로 추출합니다.")
    void getId() {
        // given
        String email = "test@test.com";
        Long id = 1L;
        String token = jwtProvider.createAccessToken(email, id);

        // when
        Long extractedId = jwtProvider.getId(token);

        // then
        assertThat(extractedId).isEqualTo(id);
    }

    @Test
    @DisplayName("토큰에서 클레임을 성공적으로 추출합니다.")
    void getClaims() {
        // given
        String email = "test@test.com";
        Long id = 1L;
        String token = jwtProvider.createAccessToken(email, id);

        // when
        Claims claims = jwtProvider.getClaims(token);

        // then
        assertThat(claims.getSubject()).isEqualTo(email);
        assertThat(claims.getId()).isEqualTo(id.toString());
    }
}
