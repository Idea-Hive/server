package Idea.Idea_Hive.member.entity.repository;

import Idea.Idea_Hive.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepo extends JpaRepository<Member, Long> {
}