package Idea.Idea_Hive.email.controller;

import Idea.Idea_Hive.email.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailAuthService authService;

    @PostMapping("/signup/send")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        authService.sendSignUpAuthCode(email);
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    @PostMapping("/signup/verify")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        if (authService.verifySignUpAuthCode(email, code)) {
            return ResponseEntity.ok("이메일 인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 올바르지 않거나 만료되었습니다.");
        }
    }
}
