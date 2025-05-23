package Idea.Idea_Hive.email.service;

import Idea.Idea_Hive.member.entity.repository.MemberJpaRepo;
import Idea.Idea_Hive.redis.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final RedisDao redisDao;
    private final MemberJpaRepo memberJpaRepo;
    private static final long EXPIRE_MINUTES = 5;


    public void sendSignUpAuthCode(String email) {

        /* 예외처리 추가 : 이미 존재하는 회원일 경우 예외 발생 */
        if (memberJpaRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        String code = generateCode(6);

        // Redis에 저장 (key = email:abc@naver.com, TTL = 5분)
        String key = "signup:" + email;
        redisDao.setValues(key, code, Duration.ofMinutes(EXPIRE_MINUTES));

        // 이메일 발송
        // todo: 메일 내용 수정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드 : " + code);
        mailSender.send(message);
    }

    public boolean verifySignUpAuthCode(String email, String code) {
        String key = "signup:" + email;
        String storedCode = redisDao.getValues(key);
        if (storedCode != null && storedCode.equals(code)) {
            redisDao.deleteValues(key); // 한 번 인증되면 삭제
            return true;
        }
        return false;
    }

    public void sendPasswordResetAuthCode(String email) {
        String key = "password-reset:" + email;
        String code = generateCode(5);
        redisDao.setValues(key, code, Duration.ofMinutes(EXPIRE_MINUTES));

        // 이메일 발송
        // todo: 메일 내용 수정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 찾기 인증 코드 발송 안내");
        message.setText("인증 코드 : " + code);
        mailSender.send(message);
    }

    public boolean verifyPasswordResetAuthCode(String email, String code) {
        String key = "password-reset:" + email;
        String storedCode = redisDao.getValues(key);
        if (storedCode != null && storedCode.equals(code)) {
            redisDao.deleteValues(key); // 한 번 인증되면 삭제
            return true;
        }
        return false;
    }

    private String generateCode(int size) {
        return UUID.randomUUID().toString().substring(0, size);
    }
}
