package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

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

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private ProjectDetail projectDetail;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkillStack> projectSkillStacks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    private Integer likedCnt;

    @OneToMany(mappedBy = "project")
    private List<ProjectApplications> projectApplications = new ArrayList<>();

    @Builder
    public Project(String title, String description,String contact, Integer maxMembers,LocalDateTime dueDateFrom,LocalDateTime dueDateTo,Boolean isSave) {
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

    // 스킬스택 추가 메서드
    public void addSkillStack(SkillStack skillstack) {
        ProjectSkillStack projectSkillStack = ProjectSkillStack.builder()
                .project(this)
                .skillstack(skillstack)
                .build();
        this.projectSkillStacks.add(projectSkillStack);
    }

    //해시태그 연관관계 편의 메서드
    public void addHashtag(String name) {
        Hashtag hashtag = Hashtag.builder()
                .project(this)
                .name(name)
                .build();
        this.hashtags.add(hashtag);
    }

    // 임시저장된 프로젝트 업데이트
    public void updateTemporaryProject(String title, String description,String contact, Integer maxMembers,
                                       LocalDateTime dueDateFrom, LocalDateTime dueDateTo, Boolean isSave) {
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
    // Project 엔티티
    public void setProjectDetail(ProjectDetail projectDetail) {
        this.projectDetail = projectDetail;
        projectDetail.setProject(this);  // 양방향 연관관계 설정
    }

    // ProjectMember 추가 메서드
    public ProjectMember addProjectMember(Member member, Role role, boolean isProfileShared, LocalDateTime profilesharedDate, boolean isLike) {
        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(this.id)
                .memberId(member.getId())
                .build();

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(this)
                .member(member)
                .role(role)
                .isProfileShared(isProfileShared)
                .profileSharedDate(profilesharedDate)
                .isLike(isLike)
                .build();

        this.projectMembers.add(projectMember);
        return projectMember;
    }

    //조회수 증가
    public void increaseViewCnt() {
        this.viewCnt++;
    }
}
