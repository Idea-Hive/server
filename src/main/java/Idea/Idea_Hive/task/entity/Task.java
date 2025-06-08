package Idea.Idea_Hive.task.entity;

import Idea.Idea_Hive.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 필수/선택 true일 경우 필수, false일 경우 서너택
    private Boolean isRequired;

    // 제출 여부
    private Boolean isSubmitted;

    private String title;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    // 업로드된 파일 경로
    private String filePath;

    // 담당자 (person in charge)
//    @Setter
//    private String pic;

    // 마감 기한
    @Setter
    private Date dueDate;

    // 제출 시간
    private Date uploadDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;
}
