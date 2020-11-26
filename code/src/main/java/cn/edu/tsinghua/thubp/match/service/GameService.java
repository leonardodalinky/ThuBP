package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.AutoModifyUtil;
import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Round;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.plugin.GameResult;
import cn.edu.tsinghua.thubp.plugin.MatchType;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboard;
import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboardConfig;
import cn.edu.tsinghua.thubp.plugin.exception.PluginErrorCode;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.GameCreateRequest;
import cn.edu.tsinghua.thubp.web.request.GameDeleteRequest;
import cn.edu.tsinghua.thubp.web.request.GameModifyRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import cn.edu.tsinghua.thubp.web.service.TokenGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Objects;

import static cn.edu.tsinghua.thubp.plugin.PluginRegistryService.MATCH_TYPE_ID;
import static cn.edu.tsinghua.thubp.plugin.PluginRegistryService.SCOREBOARD_TYPE_ID;

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
    private final PluginRegistryService pluginRegistryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 在指定轮次中，增加新的 game
     * @param userId 用户 ID
     * @param matchId 赛事 ID
     * @param roundId 轮次 ID
     * @param gameCreateRequest 比赛创建请求
     * @return 新比赛的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String createGame(String userId, String matchId, String roundId, GameCreateRequest gameCreateRequest) {
        // 校验 user 是否有权限且 roundId 合法
        boolean ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                Criteria.where("matchId").is(matchId),
                Criteria.where("active").is(true),
                Criteria.where("organizerUserId").is(userId),
                Criteria.where("rounds").all(roundId)
        )), Match.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, USER_ID, userId, ROUND_ID, roundId));
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
                .status((gameCreateRequest.getUnit1() == null)? GameStatus.WIN_FIRST : GameStatus.NOT_START)
                .unit0(gameCreateRequest.getUnit0())
                .unit1(gameCreateRequest.getUnit1())
                .build();
        mongoTemplate.save(game);
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("roundId").is(roundId)
        ), new Update().push("games", game.getGameId()), Round.class).getModifiedCount();
        if (cnt != 1) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
        return game.getGameId();
    }

    /**
     * 在指定轮次中，修改 game
     * @param userId 用户 ID
     * @param matchId 赛事 ID
     * @param roundId 轮次 ID
     * @param gameModifyRequest 比赛修改请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyGame(String userId, String matchId, String roundId, String gameId, GameModifyRequest gameModifyRequest) {
        // 校验合法
        Match match = mongoTemplate.findOne(Query.query(new Criteria().andOperator(
                Criteria.where("matchId").is(matchId),
                Criteria.where("active").is(true),
                Criteria.where("organizerUserId").is(userId),
                Criteria.where("rounds").all(roundId)
        )), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        boolean ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                Criteria.where("roundId").is(roundId),
                Criteria.where("games").all(gameId)
        )), Round.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
        // 获得 game
        Game game = mongoTemplate.findOne(Query.query(
                Criteria.where("gameId").is(gameId)
        ), Game.class);
        if (game == null) {
            throw new CommonException(MatchErrorCode.GAME_NOT_FOUND, ImmutableMap.of(GAME_ID, gameId));
        }
        // 自动修改，目前只改变 gameStatus
        AutoModifyUtil.autoModify(gameModifyRequest, game);
        // unit0 与 unit1 的修改
        if (gameModifyRequest.getUnit1() == null && gameModifyRequest.getUnit0() != null) {
            ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                    Criteria.where("roundId").is(roundId),
                    Criteria.where("units").all(gameModifyRequest.getUnit0())
            )), Round.class);
            game.setUnit0(gameModifyRequest.getUnit0());
        } else if (gameModifyRequest.getUnit0() == null && gameModifyRequest.getUnit1() != null) {
            ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                    Criteria.where("roundId").is(roundId),
                    Criteria.where("units").all(gameModifyRequest.getUnit1())
            )), Round.class);
            game.setUnit0(gameModifyRequest.getUnit1());
        } else if (gameModifyRequest.getUnit0() != null && gameModifyRequest.getUnit1() != null) {
            ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                    Criteria.where("roundId").is(roundId),
                    Criteria.where("units").all(gameModifyRequest.getUnit0(), gameModifyRequest.getUnit1())
            )), Round.class);
            game.setUnit0(game.getUnit0());
            game.setUnit1(game.getUnit1());
        } else {
            ret = true;
        }
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_UNIT_INVALID,
                    ImmutableMap.of(
                            UNIT0, gameModifyRequest.getUnit0(),
                            UNIT1, gameModifyRequest.getUnit1()
                    ));
        }
        // 配置比赛记分板项目
        MatchType matchType = pluginRegistryService.getMatchType(match.getMatchTypeId());
        if (matchType == null) {
            throw new CommonException(PluginErrorCode.MATCH_TYPE_NOT_FOUND, ImmutableMap.of(MATCH_TYPE_ID, match.getMatchTypeId()));
        }
        PluginRegistryService.ScoreboardInfo scoreboardInfo;
        GameScoreboardConfig configObject = null;
        if (game.getScoreboardTypeId() == null) {
            if (gameModifyRequest.getScoreboardTypeId() != null) {
                scoreboardInfo = pluginRegistryService.getScoreboardInfo(match.getMatchTypeId(), gameModifyRequest.getScoreboardTypeId());
                if (!Objects.equals(game.getScoreboardTypeId(), scoreboardInfo.getScoreboardTypeId())) {
                    game.setScoreboardTypeId(scoreboardInfo.getScoreboardTypeId());
                    // 检查记分板配置
                    if (game.getScoreboardConfig() != null) {
                        // configObject = scoreboardInfo.convertTreeToConfig(game.getScoreboardConfig());
                        configObject = game.getScoreboardConfig();
                        // 如果不能重用，直接清除
                        if (!scoreboardInfo.getConfigType().isAssignableFrom(configObject.getClass())) {
                            game.setScoreboardConfig(null);
                        }
                    }
                }
            }
            scoreboardInfo = matchType.getDefaultScoreboardInfo();
            game.setScoreboardTypeId(scoreboardInfo.getScoreboardTypeId());
        } else {
            scoreboardInfo = pluginRegistryService.getScoreboardInfo(match.getMatchTypeId(), game.getScoreboardTypeId());
        }
        if (game.getScoreboardConfig() == null) {
            if (gameModifyRequest.getScoreboardConfig() != null) {
                configObject = (GameScoreboardConfig) scoreboardInfo.convertTreeToConfig(gameModifyRequest.getScoreboardConfig());
                // 如果我们想要设置的 config object 格式有误，通知客户端.
                if (configObject == null) {
                    throw new CommonException(PluginErrorCode.SCOREBOARD_CONFIG_NOT_VALID, ImmutableMap.of(SCOREBOARD_TYPE_ID, game.getScoreboardTypeId()));
                }
                game.setScoreboardConfig(configObject);
            } else {
                configObject = (GameScoreboardConfig) scoreboardInfo.buildDefaultConfig();
                game.setScoreboardConfig(configObject);
            }
        } else if (configObject == null) {
            configObject = game.getScoreboardConfig();
        }
        if (gameModifyRequest.getResult() != null) {
            GameResult resultObject = (GameResult) scoreboardInfo.convertTreeToInput(gameModifyRequest.getResult());
            if (resultObject == null) {
                throw new CommonException(PluginErrorCode.SCOREBOARD_INPUT_NOT_VALID, ImmutableMap.of(
                        SCOREBOARD_TYPE_ID, scoreboardInfo.getScoreboardTypeId()
                ));
            }
            // 判断记分板格式是否正确.
            // 大部分情况下，只要能正常反序列化为 JsonValue，结果多半是合法的.
            GameScoreboard.ValidationResult result = scoreboardInfo.verifyInput(configObject, resultObject);
            if (!result.isValid()) {
                String message = result.getMessage();
                if (message != null) {
                    throw new CommonException(PluginErrorCode.SCOREBOARD_INPUT_NOT_VALID, ImmutableMap.of(
                            "message", message
                    ));
                }
                throw new CommonException(PluginErrorCode.SCOREBOARD_INPUT_NOT_VALID, null);
            }
            game.setResult(resultObject);
        }
        // 保存 game
        mongoTemplate.save(game);
    }

    /**
     * 删除一个 round 中的 game
     * @param userId 用户 Id
     * @param matchId 赛事 ID
     * @param roundId 轮次 ID
     * @param gameDeleteRequest 删除比赛的请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGame(String userId, String matchId, String roundId, GameDeleteRequest gameDeleteRequest) {
        // 校验 user 是否有权限且 roundId 合法
        boolean ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                Criteria.where("matchId").is(matchId),
                Criteria.where("active").is(true),
                Criteria.where("organizerUserId").is(userId),
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
                        Criteria.where("units").all(gameDeleteRequest.getGames())
                )
        ), Round.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_UNIT_INVALID, ImmutableMap.of(UNITS, gameDeleteRequest.getGames()));
        }
        // 删除
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("roundId").is(roundId)
        ), new Update().pullAll("games", gameDeleteRequest.getGames().toArray()), Round.class).getModifiedCount();
        if (cnt == 0) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND,
                    ImmutableMap.of(ROUND_ID, roundId, GAMES, gameDeleteRequest.getGames()));
        }
        cnt = mongoTemplate.remove(Query.query(
                Criteria.where("gameId").in(gameDeleteRequest.getGames())
        ), Game.class).getDeletedCount();
        if (cnt == 0) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND,
                    ImmutableMap.of(GAMES, gameDeleteRequest.getGames()));
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

    /**
     * 通过 {@link Game} 实例，获得反序列化后的比赛结果.
     *
     * @param game Game 实例
     * @return 比赛结果
     */
    public GameResult deserializeGameResultFromGame(Game game) {
        if (game.getScoreboardTypeId() == null || game.getScoreboardConfig() == null || game.getResult() == null) {
            return null;
        }
        return game.getResult();
    }
}
