package Idea.Idea_Hive.task.entity;

import Idea.Idea_Hive.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 필수/선택 true일 경우 필수, false일 경우 서너택
    private Boolean isRequired;

    // 제출 여부
    @Setter
    private Boolean isSubmitted;

    private String title;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    // 업로드된 파일 경로 또는 S3 Key
    @Setter
    private String filePath;

    // 사용자가 업로드한 원본 파일 이름
    @Setter
    private String originalFileName;

    // 마감 기한
    @Setter
    private LocalDateTime dueDate;

    // 과제 파일 제출 시간
    private Date fileUploadDate;

    // 과제 링크 제출 시간
    private Date linkAttachedDate;

    // 과제 링크(링크 업로드)
    private String attachedLink;

    // 과제 파일 링크(파일 업로드)
    private String fileUploadLink;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> projectTasks = new ArrayList<>();



    @Builder
    public Task(Boolean isRequired, Boolean isSubmitted, String title, TaskType taskType, String filePath, LocalDateTime dueDate,
                String fileUploadLink) {
        this.isRequired = isRequired;
        this.isSubmitted = isSubmitted;
        this.title = title;
        this.taskType = taskType;
        this.filePath = filePath;
        this.dueDate = dueDate;
        this.fileUploadLink = fileUploadLink;
    }

    public void uploadFile(String filePath, String originalFileName) {
        this.isSubmitted = true;
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.fileUploadDate = new Date();
        this.fileUploadLink = "https://taskmate-bucket2.s3.ap-northeast-2.amazonaws.com/"+filePath;
    }

    public void attachLink(String attachedLink) {
        this.isSubmitted = true;
        this.attachedLink = attachedLink;
        this.linkAttachedDate = new Date();
    }

}
