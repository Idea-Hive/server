package Idea.Idea_Hive.project.entity.repository;

import Idea.Idea_Hive.hashtag.entity.QHashtag;
import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.QMember;
import Idea.Idea_Hive.project.dto.response.ProjectInfoResponse;
import Idea.Idea_Hive.project.dto.response.ProjectTempSavedInfoResponse;
import Idea.Idea_Hive.project.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ProjectRepositoryImpl implements ProjectRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectApplicationsRepository projectApplicationsRepository;

    @Override
    public Page<ProjectApplications> findApplicantInfoById(Long projectId, Pageable pageable) {
        QProject project = QProject.project;
        QProjectApplications projectApplications = QProjectApplications.projectApplications;

        Project foundProject = queryFactory
                .selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        if (foundProject == null) {
            throw new IllegalArgumentException("존재하지 않는 프로젝트입니다.");
        }

        //프로젝트 지원자 목록 조회
        List<ProjectApplications> applications = queryFactory
                .selectFrom(projectApplications)
                .where(projectApplications.project.id.eq(projectId))
                .orderBy(projectApplications.applicationDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //전체 지원자 수 조회
        Long total = queryFactory
                .select(projectApplications.count())
                .from(projectApplications)
                .where(projectApplications.project.id.eq(projectId))
                .fetchOne();


        return new PageImpl<>(applications, pageable, total);
    }

    //임시저장된 프로젝트 정보 상세 조회
    @Override
    public ProjectTempSavedInfoResponse findTempSavedProjectInfoById(Long projectId) {
        QProject project = QProject.project;

        Project foundProject = queryFactory
                .selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        if (foundProject == null) {
            throw new IllegalArgumentException("존재하지 않는 프로젝트입니다.");
        }

        return ProjectTempSavedInfoResponse.from(foundProject);
    }

    //조회수 증가
    @Override
    public void increaseViewCnt(Long projectId) {
        QProject project = QProject.project;

        queryFactory
                .update(project)
                .set(project.viewCnt, project.viewCnt.add(1))
                .where(project.id.eq(projectId))
                .execute();
    }

    @Override
    public ProjectInfoResponse findProjectInfoById(Long projectId, Long userId) {
        QProject project = QProject.project;
        QMember member = QMember.member;

        Boolean isLike = false;
        Boolean isApply = false;

        Project foundProject = queryFactory
                .selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        if (foundProject == null) {
            throw new IllegalArgumentException("존재하지 않는 프로젝트입니다.");
        }

        if (userId != null) {
            Member foundMember = queryFactory
                    .selectFrom(member)
                    .where(member.id.eq(userId))
                    .fetchOne();

            if (foundMember == null) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다.");
            }

            ProjectMemberId projectMemberId = ProjectMemberId.builder()
                    .projectId(projectId)
                    .memberId(userId)
                    .build();

            // 들어온 프로젝트에 대해 유저의 찜 여부
            Optional<ProjectMember> optionalProjectMember = projectMemberRepository.findById(projectMemberId);

            if (optionalProjectMember.isPresent() && optionalProjectMember.get().isLike()) {
                isLike = true;
            }

            // 들어온 프로젝트에 대해 유저의 지원 상태
            QProjectApplications projectApplications = QProjectApplications.projectApplications;
            ProjectApplications latestApplications = queryFactory
                    .selectFrom(projectApplications)
                    .where(
                            projectApplications.project.id.eq(projectId),
                            projectApplications.member.id.eq(userId)
                    )
                    .orderBy(projectApplications.applicationDate.desc())
                    .fetchFirst();

            // 지원하기 가능한 상태:
            // 1. ProjectApplications 값이 없을 경우
            // 2. ProjectApplications 테이블에서 IsAccept 값이 REJECTED 인 경우
            if (latestApplications == null) {
                isApply = false;
            } else if (latestApplications.getIsAccepted() == IsAccepted.REJECTED) {
                isApply = false;
            } else {
                // 지원하기 불가능한 상태:
                // ProjectApplications 테이블에서 IsAccept 값이 CONFIRMED, UNDECIDED 인 경우
                isApply = true;
            }
        }

        // 프로젝트 리더의 완료된 프로젝트 개수
        Long completedProjectCnt = foundProject.getProjectMembers().stream()
                .filter(pm -> pm.getRole() == Role.LEADER)
                .findFirst()
                .map(leader -> {
                    String jpql = "SELECT COUNT(p) FROM Project p " +
                            "Join p.projectMembers pm " +
                            "WHERE pm.member.id = :memberId " +
                            "AND p.status = 'COMPLETED'";
                    return ((Long) entityManager.createQuery(jpql)
                            .setParameter("memberId", leader.getMember().getId())
                            .getSingleResult());
                })
                .orElse(0L);

        return ProjectInfoResponse.from(foundProject,completedProjectCnt, isLike, isApply);
    }

    @Override
    public Page<Project> searchByKeyword(String keyword, String recruitType, String sortType, Pageable pageable) {
        QProject project = QProject.project;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();

        //모집 상태 조건
        if (StringUtils.hasText(recruitType)) {
            builder.and(createRecruiteCondition(recruitType, project));
        }

        List<Project> projects = queryFactory
                .selectFrom(project)
                .distinct()
                .leftJoin(project.hashtags, hashtag)
                .where(
                        builder,
                        project.isSave.eq(true),
                        keyword != null ?
                                project.title.contains(keyword)
                                        .or(project.hashtags.any().name.contains(keyword))
                                : null
                )
                .orderBy(createOrderSpecifier(sortType, project))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.countDistinct())
                .from(project)
                .leftJoin(project.hashtags, hashtag)
                .where(
                        builder,
                        project.isSave.eq(true),
                        keyword != null ?
                                project.title.contains(keyword)
                                        .or(project.hashtags.any().name.contains(keyword))
                                : null
                )
                .fetchOne();

        return new PageImpl<>(projects, pageable, total);

    }

    // 모집 상태에 따른 조건 생성
    private BooleanExpression createRecruiteCondition(String recruitType, QProject project) {
        if (recruitType == null) return null;

        switch (recruitType) {
            case "NEW":
                return project.status.eq(ProjectStatus.RECRUITING)
                        .and(project.isNew.isTrue());
            case "ADDITIONAL":
                return project.status.eq(ProjectStatus.RECRUITING)
                        .and(project.isNew.isFalse());
            case "ALL":
                return project.status.eq(ProjectStatus.RECRUITING);
            default:
                return null;
        }
    }

    // 최신순, 마감임박순 정렬
    private OrderSpecifier<?>[] createOrderSpecifier(String sortType, QProject project) {
        if ("RECENT".equals(sortType)) {
            return new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, project.searchDate),
                    new OrderSpecifier<>(Order.DESC, project.createdDate)
            };
        } else {
            return new OrderSpecifier<?>[]{
                    new OrderSpecifier<>(Order.DESC, project.searchDate),
                    new OrderSpecifier<>(Order.DESC, project.expirationDate)
            };
        }
    }

    //memberId로 사용자의 완료된 프로젝트 개수 count
    @Override
    public Long countCompletedProjectByMemberId(Long memberId) {
        QProject project = QProject.project;

        return queryFactory
                .select(project.count())
                .from(project)
                .join(project.projectMembers)
                .where(project.projectMembers.any().member.id.eq(memberId)
                        .and(project.status.eq(ProjectStatus.COMPLETED)))
                .fetchOne();
    }
}
