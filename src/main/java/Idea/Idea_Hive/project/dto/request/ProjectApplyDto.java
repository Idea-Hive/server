package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.project.entity.IsAccepted;
import Idea.Idea_Hive.project.entity.Project;
import Idea.Idea_Hive.project.entity.ProjectApplications;

import java.time.LocalDateTime;

public record ProjectApplyDto(
        Long projectId,
        Long memberId,
        String applicationMessage,
        IsAccepted isAccepted,
        LocalDateTime applicationDate,
        Boolean isReApplication,
        String preRejectionMessage
) {
    public ProjectApplications toEntity(Project project, Member member) {
        return ProjectApplications.builder()
                .project(project)
                .member(member)
                .applicationMessage(this.applicationMessage)
                .isAccepted(this.isAccepted)
                .applicationDate(this.applicationDate)
                .isReApplication(this.isReApplication)
                .preRejectionMessage(this.preRejectionMessage)
                .build();
    }
}
