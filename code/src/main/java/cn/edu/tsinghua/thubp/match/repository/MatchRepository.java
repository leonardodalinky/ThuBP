package cn.edu.tsinghua.thubp.match.repository;

import cn.edu.tsinghua.thubp.match.entity.Match;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Link
 */
@Repository
public interface MatchRepository extends MongoRepository<Match, Integer> {
    List<Match> findAllByMatchIdIn(List<String> userIds, Pageable pageable);
}
