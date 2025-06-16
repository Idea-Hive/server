package Idea.Idea_Hive.member.entity.repository;

import Idea.Idea_Hive.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findMemberByProject_with_querydsl(Long projectId);
}
