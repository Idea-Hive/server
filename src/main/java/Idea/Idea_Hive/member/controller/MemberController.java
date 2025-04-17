package Idea.Idea_Hive.member.controller;

import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpController(@RequestBody SignUpRequest request) throws Exception {
        // todo: 입력 값 유효성 체크


        // todo: Service 호출
        signUpService.signUp(request);

        return ResponseEntity.ok("회원가입되었어요");
    }
}
