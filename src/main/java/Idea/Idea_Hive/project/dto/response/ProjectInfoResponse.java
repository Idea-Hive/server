package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectStatus;
import Idea.Idea_Hive.project.entity.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInfoResponse {
    private Long projectId;
    private String title;
    private List<String> hashtagNames;
    private Long creatorId;
    private String creatorName;
    private String creatorJob;
    private Integer creatorCareer;
    private Long creatorCompletedProjectCnt;
    private List<String> projectSkillStacks;
    private String description;
    private String idea;
    private Integer maxMembers;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private String contact;
    private Integer likedCnt;
    private Integer viewCnt;
    private LocalDateTime expirationDate;
    private ProjectStatus projectStatus;

    public static ProjectInfoResponse from(Project project, Long creatorCompletedProjectCnt) {
        ProjectInfoResponse info = new ProjectInfoResponse();
        info.projectId = project.getId();
        info.title = project.getTitle();
        info.hashtagNames = project.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());
        project.getProjectMembers().stream()
                .filter(pm -> pm.getRole() == Role.LEADER)
                .findFirst()
                .ifPresent(leader -> {
                    info.creatorId = leader.getMember().getId();
                    info.creatorName = leader.getMember().getName();
                    info.creatorJob = leader.getMember().getJob();
                    info.creatorCareer = leader.getMember().getCareer();
                });

        info.creatorCompletedProjectCnt = creatorCompletedProjectCnt;

        info.projectSkillStacks = project.getProjectSkillStacks().stream()
                .map(projectSkillStack -> projectSkillStack.getSkillstack().getName())
                .collect(Collectors.toList());

        info.description = project.getDescription();
        info.idea = project.getProjectDetail().getIdea();
        info.maxMembers = project.getMaxMembers();
        info.dueDateFrom = project.getDueDateFrom();
        info.dueDateTo = project.getDueDateTo();
        info.contact = project.getContact();
        info.likedCnt = project.getLikedCnt();
        info.viewCnt = project.getViewCnt();
        info.expirationDate = project.getExpirationDate();
        info.projectStatus = project.getStatus();
        return info;
    }
}
