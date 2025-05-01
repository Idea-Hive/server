package Idea.Idea_Hive.exception.handler.custom;

public class SkillStackAlreadyExistsException extends RuntimeException {
    public SkillStackAlreadyExistsException(String name) {
        super("이미 존재하는 SkillStack 입니다: " + name);
    }
}
