package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Round;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.GameCreateRequest;
import cn.edu.tsinghua.thubp.web.request.GameDeleteRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameService {
    public static final String USER_ID = "userId";
    public static final String MATCH_ID = "matchId";
    public static final String REFEREE_TOKEN = "refereeToken";
    public static final String UNIT_ID = "unitId";
    public static final String ROUND_ID = "roundId";
    public static final String TOKEN = "token";
    public static final String UNITS = "units";
    public static final String UNIT0 = "unit0";
    public static final String UNIT1 = "unit1";
    public static final String GAMES = "games";
    public static final String GAME_ID = "gameId";
    public static final int TOKEN_LENGTH = 6;
    public static final int EXPIRATION_DAYS = 7;

    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;
    private final TokenGeneratorService tokenGeneratorService;

    /**
     * 在指定轮次中，增加新的 game
     * @param user 用户
     * @param matchId 赛事 ID
     * @param roundId 轮次 ID
     * @param gameCreateRequest 比赛创建请求
     * @return 新比赛的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String createGame(User user, String matchId, String roundId, GameCreateRequest gameCreateRequest) {
        // 校验 user 是否有权限且 roundId 合法
        boolean ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                Criteria.where("matchId").is(matchId),
                Criteria.where("organizerUserId").is(user.getUserId()),
                Criteria.where("rounds").all(roundId)
        )), Match.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, null);
        }
        // 检验 gameCreateRequest 的合法性
        if (gameCreateRequest.getUnit1() == null) {
            ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                    Criteria.where("roundId").is(roundId),
                    Criteria.where("units").all(gameCreateRequest.getUnit0())
            )), Round.class);
        } else {
            ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                    Criteria.where("roundId").is(roundId),
                    Criteria.where("units").all(gameCreateRequest.getUnit0(), gameCreateRequest.getUnit1())
            )), Round.class);
        }
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_UNIT_INVALID,
                    ImmutableMap.of(
                            UNIT0, gameCreateRequest.getUnit0(),
                            UNIT1, gameCreateRequest.getUnit1()
                    ));
        }
        // 生成 game
        Game game = Game
                .builder()
                .gameId(sequenceGeneratorService.generateSequence(Game.SEQUENCE_NAME))
                .status(GameStatus.NOT_START)
                .unit0(gameCreateRequest.getUnit0())
                .unit1(gameCreateRequest.getUnit1())
                .build();
        mongoTemplate.save(game);
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("roundId").is(roundId)
        ), new Update().push("units", game.getGameId()), Round.class).getModifiedCount();
        if (cnt != 1) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
        return game.getGameId();
    }

    /**
     * 删除一个 round 中的 game
     * @param user 用户
     * @param matchId 赛事 ID
     * @param roundId 轮次 ID
     * @param gameDeleteRequest 删除比赛的请求
     */
    public void deleteGame(User user, String matchId, String roundId, GameDeleteRequest gameDeleteRequest) {
        // 校验 user 是否有权限且 roundId 合法
        boolean ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                Criteria.where("matchId").is(matchId),
                Criteria.where("organizerUserId").is(user.getUserId()),
                Criteria.where("rounds").all(roundId)
        )), Match.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, null);
        }
        // 检验 gameDeleteRequest 的合法性
        List<String> us = gameDeleteRequest.getGames();
        ret = mongoTemplate.exists(Query.query(
                new Criteria().andOperator(
                        Criteria.where("roundId").is(roundId),
                        Criteria.where("units").all(gameDeleteRequest)
                )
        ), Round.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_UNIT_INVALID, ImmutableMap.of(UNITS, gameDeleteRequest.getGames()));
        }
        // 删除
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("roundId").is(roundId)
        ), new Update().pullAll("units", gameDeleteRequest.getGames().toArray()), Round.class).getModifiedCount();
        if (cnt == 0) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
    }

    /**
     * 找寻一个 Game
     * @param gameId game id
     * @return Game
     */
    public Game findByGameId(String gameId) {
        Game ret = mongoTemplate.findOne(Query.query(
                Criteria.where("gameId").is(gameId)
        ), Game.class);
        if (ret == null) {
            throw new CommonException(MatchErrorCode.GAME_NOT_FOUND, ImmutableMap.of(GAME_ID, gameId));
        }
        return ret;
    }

    /**
     * 通过 gameId 的列表，找到所对应的所有 Game
     * @param gameIds gameId 的列表
     * @return Game 列表
     */
    public List<Game> findByGameIds(List<String> gameIds) {
        List<Game> ret = mongoTemplate.find(Query.query(
                Criteria.where("unitId").in(gameIds)
        ), Game.class);
        if (ret.size() != gameIds.size()) {
            throw new CommonException(MatchErrorCode.GAME_NOT_FOUND, ImmutableMap.of(GAMES, gameIds));
        }
        return ret;
    }
}
