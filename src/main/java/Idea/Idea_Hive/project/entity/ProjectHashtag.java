package Idea.Idea_Hive.project.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.MemberHashtagId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_hashtag")
public class ProjectHashtag {

    @EmbeddedId
    private ProjectHashtagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Builder
    public ProjectHashtag(Project project, Hashtag hashtag) {
        this.project = project;
        this.hashtag = hashtag;
        this.id = new ProjectHashtagId(project.getId(), hashtag.getId());
    }
}
