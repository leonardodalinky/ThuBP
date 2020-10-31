package cn.edu.tsinghua.thubp.match;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.match.repository.MatchRepository;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchService {

    public static final String MATCH_ID = "matchId";

    private final MatchRepository matchRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;

    @Transactional(rollbackFor = Exception.class)
    public String save(User user, MatchCreateRequest request) {
        String matchId = sequenceGeneratorService.generateSequence(Match.SEQUENCE_NAME);
        Match match = Match.builder()
                .matchId(matchId)
                .organizerUserId(user.getUserId())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        matchRepository.save(match);
        return matchId;
    }

    /**
     * 将用户与比赛关联起来.
     * @param user 用户
     * @param matchId 赛事 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void participateIn(User user, String matchId) {
        Match match = matchRepository.findByMatchId(matchId).orElseThrow(
                () -> new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId)));
        List<String> matches = user.getParticipatedMatches();
        if (matches != null && matches.contains(matchId)) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
        addParticipant(user.getUserId(), matchId);
    }

    /**
     * 根据 matchId 查找赛事.
     * @param matchId 赛事 ID
     * @return 对应的赛事
     */
    public Match findByMatchId(String matchId) {
        return matchRepository.findByMatchId(matchId).orElseThrow(
                () -> new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId)));
    }

    /**
     * 根据 matchId 列表查找赛事列表.
     * @param matchIds matchId 列表
     * @return 对应的赛事列表
     */
    public List<Match> findMatchesByMatchIds(List<String> matchIds) {
        return matchRepository.findAllByMatchIdIn(matchIds);
    }

    private void addParticipant(String userId, String matchId) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where(userId).nin("participants")
                )),
                new Update().push("participants", userId), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
        long userUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where(matchId).nin("participatedMatches")
                )),
                new Update().push("participatedMatches", matchId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
    }

}
