package cn.edu.tsinghua.thubp.match.repository;

import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.RefereeToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 赛事 Repository.
 */
@Repository
public interface MatchRepository extends MongoRepository<Match, Integer> {
    Optional<Match> findByMatchId(String matchId);
    List<Match> findAllByMatchIdIn(List<String> matchIds);
}
