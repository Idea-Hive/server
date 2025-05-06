package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
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

    private String title;

    @Column(length = 100) // 간단 설명 글자 수 정해지면 수정 필요
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

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectSkillStack> projectSkillStacks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Hashtag> hashtags = new ArrayList<>();

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
    }

    // 스킬스택 추가 메서드
    public void addSkillStack(SkillStack skillStack) {
        ProjectSkillStack projectSkillStack = ProjectSkillStack.builder()
                .project(this)
                .skillStack(skillStack)
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
    }
    // Project 엔티티
    public void setProjectDetail(ProjectDetail projectDetail) {
        this.projectDetail = projectDetail;
        projectDetail.setProject(this);  // 양방향 연관관계 설정
    }
}
