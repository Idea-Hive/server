package Idea.Idea_Hive.member.entity;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_skillstack")
public class MemberSkillStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skillstack_id")
    private SkillStack skillstack;

    @Builder
    public MemberSkillStack(Member member, SkillStack skillstack) {
        this.member = member;
        this.skillstack = skillstack;
    }
}
