package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.task.entity.ProjectTask;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String name;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer maxMembers;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private LocalDateTime searchDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

    private Integer viewCnt;

    private Boolean isNew;

    private Boolean isSave; //true:저장, false:임시저장

    private LocalDateTime dueDateFrom;

    private LocalDateTime dueDateTo;

    private String contact;

    private LocalDateTime expirationDate;


    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectDetail projectDetail;


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkillStack> projectSkillStacks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    private Integer likedCnt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<ProjectApplications> projectApplications = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> ProjectTasks = new ArrayList<>();

    @Builder
    public Project(String name, String title, String description,String contact, Integer maxMembers,LocalDateTime dueDateFrom,LocalDateTime dueDateTo,Boolean isSave) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.maxMembers = maxMembers;
        this.dueDateFrom = dueDateFrom;
        this.dueDateTo = dueDateTo;
        this.createdDate = LocalDateTime.now();
        this.status = ProjectStatus.RECRUITING;
        this.isNew = true;
        this.isSave = isSave;
        this.expirationDate = this.createdDate.plusMonths(1);
        this.searchDate = LocalDateTime.now();
        this.viewCnt = 0;
        this.likedCnt = 0;
    }

    // ★★★ 1:1 관계를 위한 편의 메소드 ★★★
    public void setProjectDetail(ProjectDetail projectDetail) {
        this.projectDetail = projectDetail;
        // 관계 설정의 책임을 '주인'에게 위임
        if (projectDetail != null && projectDetail.getProject() != this) {
            projectDetail.setProject(this);
        }
    }

    // 스킬스택 추가 메서드
    public void addSkillStack(SkillStack skillstack) {
        ProjectSkillStack projectSkillStack = ProjectSkillStack.builder()
                .skillstack(skillstack)
                .build();

        projectSkillStack.setProject(this);
    }

    //해시태그 연관관계 편의 메서드
    public void addHashtag(String name) {
        Hashtag hashtag = Hashtag.builder()
                .name(name)
                .build();
        hashtag.setProject(this);
    }

    // 임시저장된 프로젝트 업데이트
    public void updateTemporaryProject(String name, String title, String description,String contact, Integer maxMembers,
                                       LocalDateTime dueDateFrom, LocalDateTime dueDateTo, Boolean isSave) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.maxMembers = maxMembers;
        this.dueDateFrom = dueDateFrom;
        this.dueDateTo = dueDateTo;
        this.createdDate = LocalDateTime.now();
        this.status = ProjectStatus.RECRUITING;
        this.isNew = true;
        this.isSave = isSave;
        this.expirationDate = this.createdDate.plusMonths(1);
        this.searchDate = LocalDateTime.now();
    }

    /*
    // Project 엔티티
    public void setProjectDetail(ProjectDetail projectDetail) {
        this.projectDetail = projectDetail;
        projectDetail.setProject(this);  // 양방향 연관관계 설정
    }
     */

    // ProjectMember 추가 메서드
    public void addProjectMember(Member member, Role role, boolean isProfileShared, LocalDateTime profilesharedDate, boolean isLike) {
        ProjectMember projectMember = ProjectMember.builder()
                .member(member)
                .role(role)
                .isProfileShared(isProfileShared)
                .profileSharedDate(profilesharedDate)
                .isLike(isLike)
                .build();

        projectMember.setProject(this);
    }

    // ProjectApplications 추가 메서드
    public ProjectApplications addProjectApplications(Long id, Member member, String message, IsAccepted isAccepted) {
        ProjectApplications projectApplications = ProjectApplications.builder()
                .id(id)
                .member(member)
                .applicationMessage(message)
                .isAccepted(isAccepted)
                .applicationDate(LocalDateTime.now())
                .build();

        projectApplications.setProject(this);
        return projectApplications;
    }

    //좋아요 수 변경
    public void increaseLikedCnt() {
        this.likedCnt++;
    }

    public void decreaseLikedCnt() {
        this.likedCnt--;
    }

    public void updateStatus(ProjectStatus status) {
        this.status = status;
    }

    public void updateIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public void updateModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void updateExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void updateSearchDate(LocalDateTime searchDate) {
        this.searchDate = searchDate;
    }

    // 프로젝트 정보 수정
    public void updateProjectInfo(String name, String title, String description,String contact, Integer maxMembers,
                                       LocalDateTime dueDateFrom, LocalDateTime dueDateTo) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.maxMembers = maxMembers;
        this.dueDateFrom = dueDateFrom;
        this.dueDateTo = dueDateTo;
        this.modifiedDate = LocalDateTime.now();
    }
}
