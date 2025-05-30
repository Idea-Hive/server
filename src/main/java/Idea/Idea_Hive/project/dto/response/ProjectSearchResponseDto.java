package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.Role;

import java.util.List;
import java.util.stream.Collectors;

public record ProjectSearchResponseDto(
        Long id,
        String title,
        String description,
        List<String> hashtagNames,
        String creator,
        int viewCnt,
        int likedCnt
) {
    public static ProjectSearchResponseDto from(Project project) {
        return new ProjectSearchResponseDto(
                project.getId(),
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
                project.getLikedCnt()
        );
    }
}