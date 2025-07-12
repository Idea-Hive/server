package Idea.Idea_Hive.member.entity;

import Idea.Idea_Hive.project.entity.ProjectMember;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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

    @Setter
    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private LocalDateTime createdDate;

    /* todo: 소셜 로그인 관련 필드 추가 */
    /* todo: 회원 정보 변경 시 수정 시간 필드 필요 시 추가 */

    private Boolean isDeleted;

    @Setter
    private String job;

    @Setter
    private Integer career;

    // 관심사
    @Setter
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSkillStack> memberSkillStacks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    private LocalDateTime modifiedDate;

    private String type; // sns 연동 유형

    private String profileUrl;

    private boolean isServiceAgreed;

    private boolean isPrivacyAgreed;

    private boolean isMarketingAgreed;



    // 스킬스택 추가 메서드
    public void addSkillStack(SkillStack skillstack) {
        MemberSkillStack memberSkillStack = MemberSkillStack.builder()
                .member(this)
                .skillstack(skillstack)
                .build();
        this.memberSkillStacks.add(memberSkillStack);
    }



    // 비밀번호 수정 메서드
    // 반드시 해싱된 패스워드여야 함 !!
    public void updatePassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    /**
     * 회원의 기존 모든 기술 스택을 제거합니다.
     * MemberSkillStack 엔티티의 생명주기가 Member에 의해 관리되므로 (CascadeType.ALL),
     * 컬렉션에서 제거하면 DB에서도 삭제됩니다.
     */
    public void clearSkillStacks() {
        if (this.memberSkillStacks != null) {
            // SkillStack과의 양방향 연관관계도 함께 정리해주는 것이 안전합니다.
            for (MemberSkillStack mss : new ArrayList<>(this.memberSkillStacks)) {
                // MemberSkillStack이 참조하는 SkillStack의 컬렉션에서도 자신을 제거
                if (mss.getSkillstack() != null) {
                    mss.getSkillstack().setMemberSkillStacks(null);
                }
            }
            // 이제 Member의 컬렉션을 비웁니다. orphanRemoval=true에 의해 DB에서 삭제됩니다.
            this.memberSkillStacks.clear();
        }
    }

    @Builder
    public Member(final String name, final String email,
                  final String password, final String job,
                  final String type, final Integer career,
                  final boolean isServiceAgreed, final boolean isPrivacyAgreed,
                  final boolean isMarketingAgreed) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.job = job;
        this.type = type;
        this.career = career;
        this.createdDate = LocalDateTime.now();
        this.isDeleted = false;
        this.isServiceAgreed = isServiceAgreed;
        this.isPrivacyAgreed = isPrivacyAgreed;
        this.isMarketingAgreed = isMarketingAgreed;
    }
}
