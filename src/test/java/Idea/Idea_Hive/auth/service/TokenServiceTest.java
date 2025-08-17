package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.auth.dto.response.TokenResponse;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.redis.RedisDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisDao redisDao;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .name("testuser")
                .build();
    }

    @Test
    @DisplayName("토큰을 성공적으로 생성합니다.")
    void createTokens() {
        // given
        String email = "test@test.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(jwtProvider.createAccessToken(email, member.getId())).thenReturn("accessToken");
        when(jwtProvider.createRefreshToken(email, member.getId())).thenReturn("refreshToken");
        when(jwtProvider.getRefreshTokenValidityInMilliseconds()).thenReturn(1000L);

        // when
        TokenResponse response = tokenService.createTokens(email);

        // then
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
        verify(redisDao, times(1)).setValues(email, "refreshToken", Duration.ofMillis(1000L));
    }

    @Test
    @DisplayName("임시 리프레시 토큰을 성공적으로 생성합니다.")
    void createTempRefreshToken() {
        // given
        String email = "test@test.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(jwtProvider.createRefreshToken(email, member.getId())).thenReturn("refreshToken");
        when(jwtProvider.getRefreshTokenValidityInMilliseconds()).thenReturn(1000L);

        // when
        String refreshToken = tokenService.createTempRefreshToken(email);

        // then
        assertThat(refreshToken).isEqualTo("refreshToken");
        verify(redisDao, times(1)).setValues(email, "refreshToken", Duration.ofMillis(1000L));
    }

    @Test
    @DisplayName("리프레시 토큰이 유효한지 검증합니다.")
    void isRefreshTokenValid() {
        // given
        String email = "test@test.com";
        String refreshToken = "refreshToken";
        when(redisDao.getValues(email)).thenReturn(refreshToken);

        // when
        boolean isValid = tokenService.isRefreshTokenValid(email, refreshToken);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰을 성공적으로 삭제합니다.")
    void deleteRefreshToken() {
        // given
        String email = "test@test.com";

        // when
        tokenService.deleteRefreshToken(email);

        // then
        verify(redisDao, times(1)).deleteValues(email);
    }
}