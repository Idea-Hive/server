package Idea.Idea_Hive.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectUpdateRequest(
        Long projectId,
        String name,
        String title,
        String description,
        String idea,
        String contact,
        Integer maxMembers,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime dueDateFrom,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        LocalDateTime dueDateTo,
        List<Long> skillStackIds,
        List<String> hashtags
) {
}
