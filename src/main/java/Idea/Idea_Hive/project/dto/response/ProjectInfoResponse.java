package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProjectInfoResponse (
        Long projectId,
        String name,
        String title,
        List<String> hashtagNames,
        Long creatorId,
        String creatorName,
        String creatorJob,
        Integer creatorCareer,
        Long creatorCompletedProjectCnt,
        List<String> projectSkillStacks,
        String description,
//        String idea,
        Integer maxMembers,
        LocalDateTime dueDateFrom,
        LocalDateTime dueDateTo,
        String contact,
        Integer likedCnt,
        Integer viewCnt,
        LocalDateTime expirationDate,
        ProjectStatus projectStatus,
        Boolean isLike,
        Boolean isApply,
        Boolean isNew,
        LocalDateTime createDate

) {
    public static ProjectInfoResponse from(Project project, Long creatorCompletedProjectCnt, Boolean isLike, Boolean isApply) {
        // 리더 정보 찾기
        var leader = project.getProjectMembers().stream()
                .filter(pm -> pm.getRole() == Role.LEADER)
                .findFirst();

        return new ProjectInfoResponse(
                project.getId(),
                project.getName(),
                project.getTitle(),
                project.getHashtags().stream()
                        .map(Hashtag::getName)
                        .collect(Collectors.toList()),
                leader.map(l -> l.getMember().getId()).orElse(null),
                leader.map(l -> l.getMember().getName()).orElse(null),
                leader.map(l -> l.getMember().getJob()).orElse(null),
                leader.map(l -> l.getMember().getCareer()).orElse(null),
                creatorCompletedProjectCnt,
                project.getProjectSkillStacks().stream()
                        .map(projectSkillStack -> projectSkillStack.getSkillstack().getName())
                        .collect(Collectors.toList()),
                project.getDescription(),
//                project.getProjectDetail().getIdea(),
                project.getMaxMembers(),
                project.getDueDateFrom(),
                project.getDueDateTo(),
                project.getContact(),
                project.getLikedCnt(),
                project.getViewCnt(),
                project.getExpirationDate(),
                project.getStatus(),
                isLike,
                isApply,
                project.getIsNew(),
                project.getCreatedDate()
        );
    }
}
