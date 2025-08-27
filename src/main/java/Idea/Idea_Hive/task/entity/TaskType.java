package Idea.Idea_Hive.task.entity;

public enum TaskType {
    PLANNING("기획"),
    DESIGN("디자인"),
    DEVELOP("개발"),
    DEPLOY("배포"),
    COMPLETE("완료");

    private final String value;

    TaskType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
