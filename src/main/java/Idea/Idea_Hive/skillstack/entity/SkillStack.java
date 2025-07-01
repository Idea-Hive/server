package Idea.Idea_Hive.skillstack.entity;

import Idea.Idea_Hive.member.entity.MemberSkillStack;
import Idea.Idea_Hive.project.entity.ProjectSkillStack;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SkillStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String category; // 1depth

    private String name; // 2depth

//    @OneToMany(mappedBy = "skillstack", cascade = CascadeType.ALL)
    @OneToMany(mappedBy="skillstack")
    @Setter
    private List<MemberSkillStack> memberSkillStacks = new ArrayList<>();

    @OneToMany(mappedBy = "skillstack", cascade = CascadeType.ALL)
    private List<ProjectSkillStack> projectSkillStacks = new ArrayList<>();

    @Builder
    public SkillStack(String category, String name) {
        this.category = category;
        this.name = name;
    }
}
