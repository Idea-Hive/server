package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.project.entity.ProjectDetail;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectCreateRequest {
    private String title;
    private String description;
    private String idea;
    private Integer maxMembers;
    private LocalDateTime dueDate;
    private String contact;
    private List<Long> skillStackIds;
    private Boolean isSave;
}
