package Idea.Idea_Hive.skillstack.entity.repository;

import Idea.Idea_Hive.skillstack.entity.SkillStack;
import Idea.Idea_Hive.skillstack.entity.dto.response.SkillStackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillStackJpaRepo extends JpaRepository<SkillStack, Long> {

    @Query("SELECT new Idea.Idea_Hive.skillstack.entity.dto.response.SkillStackResponse(s.id, s.category, s.name) FROM SkillStack s")
    List<SkillStackResponse> findAllSkillStack();
    
    boolean existsByName(String name);

    List<SkillStack> findAllById(Iterable<Long> ids);

    /* todo: 위 메서드(findAllById)랑 비교 후 하나로 통합하기 */
    List<SkillStack> findAllByIdIn(List<Long> ids);
}
