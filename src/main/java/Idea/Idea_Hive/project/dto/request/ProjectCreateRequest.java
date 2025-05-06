package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.project.entity.ProjectDetail;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectCreateRequest {
    private Long projectId;
    private Long userId;
    private String title;
    private String description;
    private String idea;
    private String contact;
    private Integer maxMembers;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private List<Long> skillStackIds;
    private List<String> hashtags;
    private Boolean isSave;
}
