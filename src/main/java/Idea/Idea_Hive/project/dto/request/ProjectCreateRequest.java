package Idea.Idea_Hive.project.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectCreateRequest (
        Long projectId,
        Long userId,
        String title,
        String description,
        String idea,
        String contact,
        Integer maxMembers,
        LocalDateTime dueDateFrom,
        LocalDateTime dueDateTo,
        List<Long> skillStackIds,
        List<String> hashtags,
        Boolean isSave
){}
