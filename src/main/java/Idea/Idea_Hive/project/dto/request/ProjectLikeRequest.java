package Idea.Idea_Hive.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectLikeRequest {
    private Long projectId;
    private Long memberId;
    private boolean isLike;
}
