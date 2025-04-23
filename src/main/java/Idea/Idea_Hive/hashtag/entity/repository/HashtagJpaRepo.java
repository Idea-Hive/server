package Idea.Idea_Hive.hashtag.entity.repository;

import Idea.Idea_Hive.hashtag.entity.Hashtag;
import Idea.Idea_Hive.hashtag.entity.dto.response.HashtagResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagJpaRepo extends JpaRepository<Hashtag, Long> {

    @Query("SELECT new Idea.Idea_Hive.hashtag.entity.dto.response.HashtagResponse(h.id, h.category, h.name) FROM Hashtag h")
    List<HashtagResponse> findAllHashtag();
    
    boolean existsByName(String name);

    List<Hashtag> findAllById(Iterable<Long> ids);
}
