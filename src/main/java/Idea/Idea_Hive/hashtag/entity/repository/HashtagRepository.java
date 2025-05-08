package Idea.Idea_Hive.hashtag.entity.repository;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    void deleteByProjectId(Long projectId);
}
