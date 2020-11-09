package cn.edu.tsinghua.thubp.match;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.TimeUtil;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.RefereeToken;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.match.repository.MatchRepository;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import cn.edu.tsinghua.thubp.web.service.TokenGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchService {

    public static final String MATCH_ID = "matchId";
    public static final int TOKEN_LENGTH = 6;
    public static final int EXPIRATION_DAYS = 7;

    private final MatchRepository matchRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;
    private final TokenGeneratorService tokenGeneratorService;

    @Transactional(rollbackFor = Exception.class)
    public String save(User user, MatchCreateRequest request) {
        String matchId = sequenceGeneratorService.generateSequence(Match.SEQUENCE_NAME);
        Match match = Match.builder()
                .matchId(matchId)
                .matchTypeId(request.getMatchTypeId())
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

    /**
     * 签发一个裁判邀请码. 这会导致之前的邀请码失效.
     * @param matchId 赛事 ID.
     * @return 成功签发的邀请码
     */
    public RefereeToken assignRefereeToken(String userId, String matchId) {
        RefereeToken token = new RefereeToken(tokenGeneratorService.generateToken(TOKEN_LENGTH),
                Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)));
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("organizerUserId").is(userId)
                )),
                new Update().set("refereeToken", token), Match.class)
                .getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        return token;
    }

    /**
     * 使用裁判邀请码成为裁判.
     * @param matchId 赛事 ID.
     * @param userId 用户 ID.
     */
    public void becomeRefereeByToken(String userId, String matchId) {
        RefereeToken token = matchRepository.findByMatchId(matchId)
                .orElseThrow(() -> new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId)))
                .getRefereeToken();
        if (token == null) {
            throw new CommonException(MatchErrorCode.MATCH_REFEREE_TOKEN_NOT_ASSIGNED, ImmutableMap.of(MATCH_ID, matchId));
        } else if (token.getExpirationTime().toEpochMilli() < System.currentTimeMillis()) {
            throw new CommonException(MatchErrorCode.MATCH_REFEREE_TOKEN_EXPIRED, ImmutableMap.of(MATCH_ID, matchId));
        }
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("referees").ne(userId)
                )),
                new Update().push("referees", userId), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_REFEREE, ImmutableMap.of(MATCH_ID, matchId));
        }
    }

    private void addParticipant(String userId, String matchId) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("participants").ne(userId)
                )),
                new Update().push("participants", userId), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
        long userUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("participatedMatches").ne(matchId)
                )),
                new Update().push("participatedMatches", matchId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
    }

}
