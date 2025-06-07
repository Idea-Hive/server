package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.project.entity.IsAccepted;

public record ProjectApplyDecisionRequest (
        Long projectId,
        Long userId,
        Long applyId,
        IsAccepted decision,
        String rejectionMessage
){}
