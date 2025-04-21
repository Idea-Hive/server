package Idea.Idea_Hive.hashtag.service;

import Idea.Idea_Hive.exception.handler.custom.HashtagAlreadyExistsException;
import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.hashtag.entity.dto.request.CreateHashtagRequest;
import Idea.Idea_Hive.hashtag.entity.dto.response.HashtagResponse;
import Idea.Idea_Hive.hashtag.entity.repository.HashtagJpaRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagJpaRepo hashtagJpaRepo;

    public List<HashtagResponse> getAllHashtag() {
        return hashtagJpaRepo.findAllHashtag();
    }

    @Transactional
    public void createHashtag(CreateHashtagRequest createHashtagRequest) {

        // 이미 존재하는 해쉬태그일 경우 에러 던짐

        if (hashtagJpaRepo.existsByName(createHashtagRequest.name())) {
            throw new HashtagAlreadyExistsException(createHashtagRequest.name());
        }

        Hashtag newHashtag = Hashtag.builder()
                .category(createHashtagRequest.category())
                .name(createHashtagRequest.name())
                .build();

        hashtagJpaRepo.save(newHashtag);
    }
}
