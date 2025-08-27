package Idea.Idea_Hive.project.entity;

public enum Role {
    LEADER("팀장"),
    TEAM_MEMBER("팀원"),
    GUEST("게스트");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
