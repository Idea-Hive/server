package Idea.Idea_Hive.hashtag.controller;

import Idea.Idea_Hive.hashtag.entity.dto.request.CreateHashtagRequest;
import Idea.Idea_Hive.hashtag.entity.dto.response.HashtagResponse;
import Idea.Idea_Hive.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hashtag")
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("")
    public ResponseEntity<List<HashtagResponse>> getAllHashtags() {
        List<HashtagResponse> hashtags = hashtagService.getAllHashtag();
        return ResponseEntity.ok(hashtags);
    }

    /* Admin에서 직접 해쉬태그 추가 할 수 있도록 하기위한 API */
    @PostMapping("")
    public ResponseEntity<String> addHashtag(@RequestBody CreateHashtagRequest createHashtagRequest) {
        hashtagService.createHashtag(createHashtagRequest);
        return ResponseEntity.ok(createHashtagRequest.name() + ": 해쉬태그 생성 성공");
    }
}
