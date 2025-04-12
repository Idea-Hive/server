package Idea.Idea_Hive.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
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

    private Boolean tempSave;

    @OneToOne
    @JoinColumn(name = "projectDetailId")
    private ProjectDetail projectDetail;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectFile> projectFiles = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addProjectFile(String fileUrl, String fileType) {
        ProjectFile file = ProjectFile.builder()
                .fileUrl(fileUrl)
                .fileType(fileType)
                .project(this)  // 생성 시점에 project 설정
                .build();
        this.projectFiles.add(file);
    }

    @Builder
    public Project(String title, String description, Integer maxMembers, Boolean tempSave) {
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
        this.createdDate = LocalDateTime.now();
        this.status = ProjectStatus.RECRUITING;
        this.isNew = true;
        this.tempSave = tempSave;
    }

}
