package Idea.Idea_Hive.task.entity;

import Idea.Idea_Hive.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

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

    // 업로드된 파일 경로
    @Setter
    private String filePath;

    // 담당자 (person in charge)
//    @Setter
//    private String pic;

    // 마감 기한
    @Setter
    private Date dueDate;

    // 제출 시간
    private Date uploadDate;

    // 첨부 링크
    private String attachedLink;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Task(Boolean isRequired, Boolean isSubmitted, String title, TaskType taskType, String filePath, Date dueDate,
                String attachedLink) {
        this.isRequired = isRequired;
        this.isSubmitted = isSubmitted;
        this.title = title;
        this.taskType = taskType;
        this.filePath = filePath;
        this.dueDate = dueDate;
        this.attachedLink = attachedLink;
    }

    public Task uploadFile(String filePath) {
        this.isSubmitted = true;
        this.filePath = filePath;
        this.uploadDate = new Date();
        return this;
    }

    public Task attachLink(String attachedLink) {
        this.isSubmitted = true;
        this.attachedLink = attachedLink;
        this.uploadDate = new Date();
        return this;
    }

}
