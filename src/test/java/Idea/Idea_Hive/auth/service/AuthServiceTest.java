package Idea.Idea_Hive.auth.service;

import Idea.Idea_Hive.auth.dto.request.EmailLoginRequest;
import Idea.Idea_Hive.auth.dto.response.AuthInfoResponse;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .name("testuser")
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("인증 정보 조회를 성공합니다.")
    void getAuthInfo() {
        // given
        EmailLoginRequest request = new EmailLoginRequest("test@test.com", "password");
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // when
        AuthInfoResponse response = authService.getAuthInfo(request);

        // then
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("잘못된 이메일로 인증 정보 조회 시 예외가 발생합니다.")
    void getAuthInfoWithInvalidEmail() {
        // given
        EmailLoginRequest request = new EmailLoginRequest("wrong@test.com", "password");
        when(memberRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.getAuthInfo(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 이메일 또는 비밀번호입니다.");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 인증 정보 조회 시 예외가 발생합니다.")
    void getAuthInfoWithInvalidPassword() {
        // given
        EmailLoginRequest request = new EmailLoginRequest("test@test.com", "wrongpassword");
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.getAuthInfo(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 이메일 또는 비밀번호입니다.");
    }

    @Test
    @DisplayName("로그인한 사용자의 ID를 성공적으로 검증합니다.")
    void verifyMemberIdIsLogined() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        boolean result = authService.verifyMemberIdIsLogined(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("로그인하지 않은 사용자의 ID 검증 시 false를 반환합니다.")
    void verifyMemberIdIsNotLogined() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("wrong@test.com", null, Collections.emptyList())
        );
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        boolean result = authService.verifyMemberIdIsLogined(1L);

        // then
        assertThat(result).isFalse();
    }
}