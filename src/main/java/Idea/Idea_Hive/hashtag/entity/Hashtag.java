package Idea.Idea_Hive.hashtag.entity;

import Idea.Idea_Hive.member.entity.MemberHashtag;
import Idea.Idea_Hive.project.entity.ProjectHashtag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String category;

    private String name;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL)
    private List<MemberHashtag> memberHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL)
    private List<ProjectHashtag> projectHashtags = new ArrayList<>();

    @Builder
    public Hashtag(String category, String name) {
        this.category = category;
        this.name = name;
    }
}
