package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// todo: 리팩토링할 때 다른 Response DTO랑 통합 (필요시)
public record ProjectManageResponse(
        Long projectId,
        String name,                    // 프로젝트명
        String title,                   // 프로젝트 게시글 제목
        String description,
        List<String> hashtagNames,
        String creator,
        Integer viewCnt,
        Integer likedCnt,
        LocalDateTime expirationDate,
        ProjectStatus status
) {
    public static ProjectManageResponse from(Project project) {
        return new ProjectManageResponse(
                project.getId(),
                project.getName(),
                project.getTitle(),
                project.getDescription(),
                project.getHashtags().stream()
                        .map(Hashtag::getName)
                        .collect(Collectors.toList()),
                project.getProjectMembers().stream()
                        .filter(pm -> pm.getRole() == Role.LEADER)
                        .findFirst()
                        .map(pm -> pm.getMember().getName())
                        .orElse("Unknown"),
                project.getViewCnt(),
                project.getLikedCnt(),
                project.getExpirationDate(),
                project.getStatus()
        );
    }
}
