package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.auth.dto.response.TokenResponse;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.redis.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;
    private final MemberRepository memberRepository;

    public TokenResponse createTokens(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 이메일 입니다.")
                );

        String accessToken = jwtProvider.createAccessToken(email, member.getId());
        String refreshToken = jwtProvider.createRefreshToken(email, member.getId());
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
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 이메일 입니다.")
                );
        return jwtProvider.createRefreshToken(email, member.getId());
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

    public void deleteRefreshToken(String email) {
        // todo: 수정 완료 시 RefreshToken Redis에서 제거, 프론트에서도 AccessToken 제거해줘야함.
        redisDao.deleteValues(email); // Refresh Token 제거
    }
}