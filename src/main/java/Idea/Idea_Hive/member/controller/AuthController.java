package Idea.Idea_Hive.member.controller;

import Idea.Idea_Hive.member.entity.dto.request.EmailLoginRequest;
import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AuthController {

    private final SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpController(@RequestBody SignUpRequest request) throws Exception {
        // todo: 입력 값 유효성 체크


        // todo: Service 호출
        signUpService.signUp(request);
        return ResponseEntity.ok("회원가입되었어요");
    }

    // todo: 임시 컨트롤러입니다.
    @GetMapping("/github")
    public String Home() {
        return "<div>" +
                "<a href='/oauth2/authorization/github'>GitHub 로그인</a>" +
                "</div>" +
                "<a href='/oauth2/authorization/google'>Google 로그인</a>";
    }

    @GetMapping("/secure")
    public String secure(@AuthenticationPrincipal OAuth2User principal) {
        return "로그인한 사용자: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Email 로그인
    @PostMapping("/login")
    public ResponseEntity<String> emailLoginController(@RequestBody EmailLoginRequest reqeust) {
        // todo: 입력 값 유효성 체크

        // todo: login 진행

        return ResponseEntity.ok("로그인 완료");
    }
}