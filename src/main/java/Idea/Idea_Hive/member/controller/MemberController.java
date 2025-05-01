package Idea.Idea_Hive.member.controller;

import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.entity.dto.response.SignUpResponse;
import Idea.Idea_Hive.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUpController(@RequestBody SignUpRequest request) throws Exception {
        // todo: 입력 값 유효성 체크
        return ResponseEntity.ok(memberService.signUp(request));
    }
}
