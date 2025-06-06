package Idea.Idea_Hive.project.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectUpdateRequest(
        Long projectId,
        String title,
        String description,
        String idea,
        String contact,
        Integer maxMembers,
        LocalDateTime dueDateFrom,
        LocalDateTime dueDateTo,
        List<Long> skillStackIds,
        List<String> hashtags
) {
}
