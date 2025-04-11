package Idea.Idea_Hive.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
public class ProjectFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

    private String fileUrl;

    private String fileType;

    private LocalDateTime uploadDate;

    //연관관계 메서드
    @Builder
    public ProjectFile(String fileUrl, String fileType, Project project) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.project = project;
    }
}
