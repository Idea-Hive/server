package Idea.Idea_Hive.member.entity.repository;

import Idea.Idea_Hive.member.entity.Member;
import Idea.Idea_Hive.member.entity.QMember;
import Idea.Idea_Hive.project.entity.QProjectMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMemberByProject_with_querydsl(Long projectId) {
        QMember member = QMember.member;
        QProjectMember projectMember = QProjectMember.projectMember;

        return queryFactory
                .select(member)
                .from(projectMember)
                .join(projectMember.member, member)
                .where(projectMember.project.id.eq(projectId))
                .fetch();
    }

}
