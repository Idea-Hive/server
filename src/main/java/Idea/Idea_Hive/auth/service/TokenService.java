package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.auth.dto.response.TokenResponse;
import Idea.Idea_Hive.redis.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;

    public TokenResponse createTokens(String email) {
        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);
        long refreshTokenExpire = jwtProvider.getRefreshTokenValidityInMilliseconds();
        redisDao.setValues(email, refreshToken, Duration.ofMillis(refreshTokenExpire));
        return new TokenResponse(accessToken, refreshToken);
    }

    public String createTempRefreshToken(String email) {
        String refreshToken = createRefreshToken(email);
        long refreshTokenExpire = jwtProvider.getRefreshTokenValidityInMilliseconds();
        redisDao.setValues(email, refreshToken, Duration.ofMillis(refreshTokenExpire));
        return refreshToken;
    }

    public String createRefreshToken(String email) {
        return jwtProvider.createRefreshToken(email);
    }

    public long getRefreshTokenValidityInMilliseconds() {
        return jwtProvider.getRefreshTokenValidityInMilliseconds();
    }

    public void validateToken(String token) {
        jwtProvider.validateToken(token);
    }

    public boolean isRefreshTokenValid(String email, String refreshToken) {
        return redisDao.getValues(email).equals(refreshToken);
    }

    public String getEmail(String token) {
        return jwtProvider.getEmail(token);
    }

}