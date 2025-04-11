package Idea.Idea_Hive.project.entity;

public enum ProjectStatus {
    RECRUITING("모집중"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
