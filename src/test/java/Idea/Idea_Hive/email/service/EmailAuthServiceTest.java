package Idea.Idea_Hive.email.service;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.repository.MemberRepository;
import Idea.Idea_Hive.redis.RedisDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailAuthServiceTest {

    @InjectMocks
    private EmailAuthService emailAuthService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RedisDao redisDao;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 인증 코드를 성공적으로 전송합니다.")
    void sendSignUpAuthCode() {
        // given
        String email = "test@test.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        emailAuthService.sendSignUpAuthCode(email);

        // then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(redisDao, times(1)).setValues(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입 인증 코드 전송 시 예외가 발생합니다.")
    void sendSignUpAuthCodeToExistingEmail() {
        // given
        String email = "test@test.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(Member.builder().build()));

        // when & then
        assertThatThrownBy(() -> emailAuthService.sendSignUpAuthCode(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 회원입니다.");
    }

    @Test
    @DisplayName("회원가입 인증 코드를 성공적으로 검증합니다.")
    void verifySignUpAuthCode() {
        // given
        String email = "test@test.com";
        String code = "123456";
        when(redisDao.getValues("signup:" + email)).thenReturn(code);

        // when
        boolean isVerified = emailAuthService.verifySignUpAuthCode(email, code);

        // then
        assertThat(isVerified).isTrue();
        verify(redisDao, times(1)).deleteValues("signup:" + email);
    }

    @Test
    @DisplayName("잘못된 회원가입 인증 코드로 검증 시 false를 반환합니다.")
    void verifySignUpAuthCodeWithInvalidCode() {
        // given
        String email = "test@test.com";
        String code = "123456";
        when(redisDao.getValues("signup:" + email)).thenReturn("wrongcode");

        // when
        boolean isVerified = emailAuthService.verifySignUpAuthCode(email, code);

        // then
        assertThat(isVerified).isFalse();
        verify(redisDao, never()).deleteValues(anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 인증 코드를 성공적으로 전송합니다.")
    void sendPasswordResetAuthCode() {
        // given
        String email = "test@test.com";

        // when
        emailAuthService.sendPasswordResetAuthCode(email);

        // then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(redisDao, times(1)).setValues(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("비밀번호 재설정 인증 코드를 성공적으로 검증합니다.")
    void verifyPasswordResetAuthCode() {
        // given
        String email = "test@test.com";
        String code = "12345";
        when(redisDao.getValues("password-reset:" + email)).thenReturn(code);

        // when
        boolean isVerified = emailAuthService.verifyPasswordResetAuthCode(email, code);

        // then
        assertThat(isVerified).isTrue();
        verify(redisDao, times(1)).deleteValues("password-reset:" + email);
    }
}
