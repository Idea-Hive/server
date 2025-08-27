package Idea.Idea_Hive.skillstack.entity.dto;

import Idea.Idea_Hive.skillstack.entity.SkillStack;

public record SkillStackVO(
        Long id,
        String category,
        String name
) {
    public static SkillStackVO from (SkillStack skillStack) {
        return new SkillStackVO(
                skillStack.getId(),
                skillStack.getCategory(),
                skillStack.getName()
        );
    }
}
