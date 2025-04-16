package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.member.entity.MemberHashtag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

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

    private LocalDateTime dueDate;

    private String contact;

    private LocalDateTime expirationDate;

    @OneToOne
    @JoinColumn(name = "projectDetailId")
    private ProjectDetail projectDetail;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectHashtag> projectHashtags = new ArrayList<>();

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

    // 해시태그 추가 메서드
    public void addHashtag(Hashtag hashtag) {
        ProjectHashtag projectHashtag = ProjectHashtag.builder()
                .project(this)
                .hashtag(hashtag)
                .build();
        this.projectHashtags.add(projectHashtag);
    }

}
