package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectResponseDto {
    private Long id;
    private String title;
    private String description;
    private List<String> hashtagNames;
    private String creator;
    private int viewCount;
    private int likedCnt;

    public static ProjectResponseDto from(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.description = project.getDescription();
        dto.hashtagNames = project.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());

        dto.creator = project.getProjectMembers().stream()
                .filter(pm -> pm.getRole() == Role.LEADER)
                .findFirst()
                .map(pm -> pm.getMember().getName())
                .orElse("Unknown");

        dto.viewCount = project.getViewCnt();
        dto.likedCnt = project.getLikedCnt();
        return dto;
    }
}
