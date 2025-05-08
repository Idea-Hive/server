package Idea.Idea_Hive.member.entity;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_skillstack")
public class MemberSkillStack {

    @EmbeddedId
    private MemberSkillStackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillstackId")
    @JoinColumn(name = "skillstack_id")
    private SkillStack skillstack;

    @Builder
    public MemberSkillStack(Member member, SkillStack skillstack) {
        this.member = member;
        this.skillstack = skillstack;
        this.id = new MemberSkillStackId(member.getId(), skillstack.getId());
    }
}
