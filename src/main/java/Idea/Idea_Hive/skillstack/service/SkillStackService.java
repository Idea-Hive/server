package Idea.Idea_Hive.skillstack.service;

import Idea.Idea_Hive.exception.handler.custom.SkillStackAlreadyExistsException;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.skillstack.entity.dto.request.CreateSkillStackRequest;
import Idea.Idea_Hive.skillstack.entity.dto.response.SkillStackResponse;
import Idea.Idea_Hive.skillstack.entity.repository.SkillStackJpaRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillStackService {

    private final SkillStackJpaRepo skillStackJpaRepo;

    public List<SkillStackResponse> getAllSkillStack() {
        return skillStackJpaRepo.findAllSkillStack();
    }

    @Transactional
    public void createHashtag(CreateSkillStackRequest createSkillStackRequest) {
        // 이미 존재하는 기술스택일 경우 에러 던짐
        if (skillStackJpaRepo.existsByName(createSkillStackRequest.name())) {
            throw new SkillStackAlreadyExistsException(createSkillStackRequest.name());
        }
        SkillStack newSkillStack = SkillStack.builder()
                .category(createSkillStackRequest.category())
                .name(createSkillStackRequest.name())
                .build();
        skillStackJpaRepo.save(newSkillStack);
    }
}
