package Idea.Idea_Hive.member.entity;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Member Entity
 * 데이터 삭제 시 컬럼 값으로만 처리, 실제 데이터는 DB에 남아있도록 하였음.
 * 컨벤션 : Entity 생성자는 Builder 패턴 사용으로 고정하면 좋을 것 같습니다.
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE id = ?") // Soft Delete
@SQLRestriction("is_deleted = false")
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private LocalDateTime createdDate;

    /* todo: 소셜 로그인 관련 필드 추가 */
    /* todo: 회원 정보 변경 시 수정 시간 필드 필요 시 추가 */

    private Boolean isDeleted;

    private String job;

    private Integer career;

    // 관심사
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberHashtag> memberHashtags = new ArrayList<>();

    private LocalDateTime modifiedDate;

    private String type; // sns 연동 유형

    private boolean isVerified;

    private String profileUrl;


    // 해시태그 추가 메서드
    public void addHashtag(Hashtag hashtag) {
        MemberHashtag memberHashtag = MemberHashtag.builder()
                .member(this)
                .hashtag(hashtag)
                .build();
        this.memberHashtags.add(memberHashtag);
    }

    @Builder
    public Member(final String name, final String email,
                  final String password, final String job,
                  final String type, final Integer career) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.job = job;
        this.type = type;
        this.career = career;
        this.createdDate = LocalDateTime.now();
        this.isDeleted = false;
    }
}
