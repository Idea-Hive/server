package Idea.Idea_Hive.member.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_hashtag")
public class MemberHashtag {

    @EmbeddedId
    private MemberHashtagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Builder
    public MemberHashtag(Member member, Hashtag hashtag) {
        this.member = member;
        this.hashtag = hashtag;
        this.id = new MemberHashtagId(member.getId(), hashtag.getId());
    }
}
