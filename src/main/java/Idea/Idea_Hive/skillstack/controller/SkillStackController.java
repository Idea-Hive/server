package Idea.Idea_Hive.skillstack.controller;

import Idea.Idea_Hive.skillstack.entity.dto.request.CreateSkillStackRequest;
import Idea.Idea_Hive.skillstack.entity.dto.response.SkillStackResponse;
import Idea.Idea_Hive.skillstack.service.SkillStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skillstack")
public class SkillStackController {

    private final SkillStackService skillstackService;

    @GetMapping("")
    public ResponseEntity<List<SkillStackResponse>> getAllStacks() {
        List<SkillStackResponse> stacks = skillstackService.getAllSkillStack();
        return ResponseEntity.ok(stacks);
    }

    /* Admin에서 직접 스킬스택 추가 할 수 있도록 하기위한 API */
    @PostMapping("")
    public ResponseEntity<String> addSkillStack(@RequestBody CreateSkillStackRequest createSkillStackRequest) {
        skillstackService.createHashtag(createSkillStackRequest);
        return ResponseEntity.ok(createSkillStackRequest.name() + ": 기술 스택 생성 성공");
    }
}
