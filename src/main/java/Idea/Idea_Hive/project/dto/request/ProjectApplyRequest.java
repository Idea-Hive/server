package Idea.Idea_Hive.project.dto.request;

import lombok.Getter;

@Getter
public class ProjectApplyRequest {
    private Long projectId;
    private Long memberId;
    private String message;
}
