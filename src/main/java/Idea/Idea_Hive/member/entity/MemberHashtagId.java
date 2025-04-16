package Idea.Idea_Hive.member.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MemberHashtagId implements Serializable {
    private Long memberId;
    private Long hashtagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())return false;
        MemberHashtagId that = (MemberHashtagId) o;
        return Objects.equals(memberId, that.memberId) &&
                Objects.equals(hashtagId, that.hashtagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, hashtagId);
    }
}
