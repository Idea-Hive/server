package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.project.entity.IsAccepted;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProjectApplyDecisionRequest {
    private Long projectId;
    private Long memberId;
    private IsAccepted decision;
    private String rejectionMessage;
}
