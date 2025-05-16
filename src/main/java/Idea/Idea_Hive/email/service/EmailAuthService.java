package Idea.Idea_Hive.email.service;

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
    private static final long EXPIRE_MINUTES = 5;


    public void sendSignUpAuthCode(String email) {
        String code = generateCode();

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

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 5);
    }
}
