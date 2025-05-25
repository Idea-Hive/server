package Idea.Idea_Hive.project.entity;

public enum IsAccepted {
    CONFIRMED("확정"),
    REJECTED("거절"),
    UNDECIDED("미정");

    private final String value;

    IsAccepted(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
