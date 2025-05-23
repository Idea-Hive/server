package Idea.Idea_Hive.member.controller;

import Idea.Idea_Hive.member.entity.dto.request.PasswordResetRequest;
import Idea.Idea_Hive.member.entity.dto.request.SignUpRequest;
import Idea.Idea_Hive.member.entity.dto.response.SignUpResponse;
import Idea.Idea_Hive.member.entity.dto.response.MemberInfoResponse;
import Idea.Idea_Hive.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    // Email을 사용한 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUpController(@RequestBody SignUpRequest request) throws Exception {
        // todo: 입력 값 유효성 체크
        return ResponseEntity.ok(memberService.signUp(request));
    }

    // todo: 비밀번호 수정 시 로그아웃 처리하기 -> (프론트에서 토큰 삭제 필요)
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        memberService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }


    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getUserInfo() {
        MemberInfoResponse memberInfoResponse = memberService.getUserInfo();
        return ResponseEntity.ok(memberInfoResponse);
    }
}
