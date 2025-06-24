package Idea.Idea_Hive.member.entity;

import Idea.Idea_Hive.project.entity.ProjectMember;
import Idea.Idea_Hive.skillstack.entity.SkillStack;
import jakarta.persistence.*;
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
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberSkillStack> memberSkillStacks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    private LocalDateTime modifiedDate;

    private String type; // sns 연동 유형

    private boolean isVerified;

    private String profileUrl;



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
        // MemberSkillStack과의 양방향 연관 관계를 끊어주고, 컬렉션을 비웁니다.
        // CascadeType.ALL과 orphanRemoval=true가 함께 설정되어 있다면 더 확실하게 동작합니다.
        // 현재는 CascadeType.ALL만 있으므로, MemberSkillStack의 member 필드를 null로 설정해주면
        // 고아 객체로 인식되어 삭제될 수 있지만, 명시적으로 컬렉션을 비우는 것이 안전합니다.
        // 주의: DB에서 MemberSkillStack 레코드를 삭제합니다.

        if (this.memberSkillStacks != null) {
            // 양방향 연관관계 제거
             for (MemberSkillStack mss : this.memberSkillStacks) {
                mss.setMember(null); // MemberSkillStack에 Setter 추가
             }
            this.memberSkillStacks.clear();
        } else {
            this.memberSkillStacks = new ArrayList<>(); // null일 경우 초기화
        }
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
