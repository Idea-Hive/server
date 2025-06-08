package Idea.Idea_Hive.member.entity.repository;

import Idea.Idea_Hive.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    public List<Member> findMembersByProjectId(Long projectId);
}
