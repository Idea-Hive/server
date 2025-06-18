package Idea.Idea_Hive.project.dto.response;

import Idea.Idea_Hive.project.entity.ProjectApplications;
import Idea.Idea_Hive.project.entity.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public record ProjectApplicantResponse (
        List<ProjectApplicantResponseDto> applicants,
        long totalCnt,
        int totalPages,
        int currentPage,
        int pageSize
){
    public static ProjectApplicantResponse of(Page<ProjectApplications> projectApplicationsPage, ProjectRepository projectRepository) {

        List<ProjectApplicantResponseDto> applicantDto = projectApplicationsPage.getContent().stream()
                .map(applications -> {
                    Long completedProjectCnt = projectRepository.countCompletedProjectByMemberId(applications.getMember().getId());
                    return ProjectApplicantResponseDto.from(applications, completedProjectCnt);
                })
                .collect(Collectors.toList());

        return new ProjectApplicantResponse(
                applicantDto,
                projectApplicationsPage.getTotalElements(),
                projectApplicationsPage.getTotalPages(),
                projectApplicationsPage.getNumber() + 1,
                projectApplicationsPage.getSize()
        );
    }
}
