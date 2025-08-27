package Idea.Idea_Hive.project.dto.response;


import java.util.List;

public record ProjectSubmitResponse(
        boolean isAllSubmitted,
        List<Long> unsubmittedTaskIds
) {
    public static ProjectSubmitResponse success() {
        return new ProjectSubmitResponse(true, List.of());
    }

    public static ProjectSubmitResponse failure(List<Long> unsubmittedTaskIds) {
        return new ProjectSubmitResponse(false, unsubmittedTaskIds);
    }
}
