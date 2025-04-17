package Idea.Idea_Hive.email.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRE_MINUTES = 5;

    public EmailAuthService(JavaMailSender mailSender, StringRedisTemplate redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    public void sendCode(String email) {
        String code = generateCode();

        // Redis에 저장 (key = email:abc@naver.com, TTL = 5분)
        String key = "email:" + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(EXPIRE_MINUTES));

        // 이메일 발송
        // todo: 메일 내용 수정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드는: " + code);
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        String key = "email:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key); // 한 번 인증되면 삭제
            return true;
        }

        return false;
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
