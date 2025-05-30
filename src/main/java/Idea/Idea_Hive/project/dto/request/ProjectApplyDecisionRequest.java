package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.project.entity.IsAccepted;

public record ProjectApplyDecisionRequest (
        Long projectId,
        Long memberId,
        IsAccepted decision,
        String rejectionMessage
){}
