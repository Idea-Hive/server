package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSearchResponseDto {
    private Long id;
    private String title;
    private String description;
    private List<String> hashtagNames;
    private String creator;
    private int viewCnt;
    private int likedCnt;

    public static ProjectSearchResponseDto from(Project project) {
        ProjectSearchResponseDto dto = new ProjectSearchResponseDto();
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

        dto.viewCnt = project.getViewCnt();
        dto.likedCnt = project.getLikedCnt();
        return dto;
    }
}
