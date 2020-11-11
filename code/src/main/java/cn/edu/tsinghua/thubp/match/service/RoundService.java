package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Round;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.match.enums.RoundGameStrategy;
import cn.edu.tsinghua.thubp.match.enums.RoundStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.RoundCreateRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import cn.edu.tsinghua.thubp.web.service.TokenGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoundService {
    public static final String USER_ID = "userId";
    public static final String MATCH_ID = "matchId";
    public static final String REFEREE_TOKEN = "refereeToken";
    public static final String UNIT_ID = "unitId";
    public static final String ROUND_ID = "roundId";
    public static final String ROUNDS = "rounds";
    public static final String TOKEN = "token";
    public static final String UNITS = "units";
    public static final String UNIT0 = "unit0";
    public static final String UNIT1 = "unit1";
    public static final int TOKEN_LENGTH = 6;
    public static final int EXPIRATION_DAYS = 7;

    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;
    private final TokenGeneratorService tokenGeneratorService;
    /**
     * 生成新的轮次
     * @param user 用户
     * @param matchId 赛事 ID
     * @param roundCreateRequest 创建轮次的请求
     * @return 新的轮次的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String createRound(User user, String matchId, RoundCreateRequest roundCreateRequest) {
        // 先检验 request 的 units
        List<String> units = roundCreateRequest.getUnits();
        boolean ret = mongoTemplate.exists(Query.query(
                new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("units").all(units)
                )
        ), Match.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_UNIT_INVALID, ImmutableMap.of(UNITS, units));
        }
        // 生成新轮次
        String roundId = sequenceGeneratorService.generateSequence(Round.SEQUENCE_NAME);
        Round round = Round.builder()
                .roundId(roundId)
                .name(roundCreateRequest.getName())
                .description(roundCreateRequest.getDescription())
                .status(RoundStatus.NOT_START)
                .units(units)
                .games(new ArrayList<>())
                .build();
        autoGenerateGame(round, roundCreateRequest.getAutoStrategy());
        mongoTemplate.save(round);

        return roundId;
    }

    /**
     * 找寻一个 Round
     * @param roundId round id
     * @return Round
     */
    public Round findByRoundId(String roundId) {
        Round ret = mongoTemplate.findOne(Query.query(
                Criteria.where("gameId").is(roundId)
        ), Round.class);
        if (ret == null) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
        return ret;
    }

    /**
     * 通过 roundId 的列表，找到所对应的所有 Round
     * @param roundIds roundId 的列表
     * @return Round 列表
     */
    public List<Round> findByRoundIds(List<String> roundIds) {
        List<Round> ret = mongoTemplate.find(Query.query(
                Criteria.where("roundId").in(roundIds)
        ), Round.class);
        if (ret.size() != roundIds.size()) {
            throw new CommonException(MatchErrorCode.GAME_NOT_FOUND, ImmutableMap.of(ROUNDS, roundIds));
        }
        return ret;
    }

    /**
     * 给 round 根据 strategy 自动加入 Game
     * 不检测 round 中已存在的 game
     * @param round Round 对象
     * @param strategy 策略
     */
    private void autoGenerateGame(Round round, RoundGameStrategy strategy) {
        List<String> units = round.getUnits();
        switch (strategy) {
            case SINGLE_ROUND:
                // 排除数量过多
                if (units.size() > 8) {
                    throw new CommonException(MatchErrorCode.ROUND_AUTO_EXCESSIVE, ImmutableMap.of(UNITS, round.getUnits().size()));
                }
                for (int i = 0;i < units.size();++i) {
                    for (int j = i + 1;j < units.size();++j) {
                        Game game = Game
                                .builder()
                                .gameId(sequenceGeneratorService.generateSequence(Game.SEQUENCE_NAME))
                                .status(GameStatus.NOT_START)
                                .unit0(units.get(i))
                                .unit1(units.get(j))
                                .build();
                        round.getGames().add(game.getGameId());
                        mongoTemplate.save(game);
                    }
                }
                break;
            case SINGLE_ROUND_HH:
                // 排除数量过多
                if (units.size() > 8) {
                    throw new CommonException(MatchErrorCode.ROUND_AUTO_EXCESSIVE, ImmutableMap.of(UNITS, round.getUnits().size()));
                }
                for (int i = 0;i < units.size();++i) {
                    for (int j = 0;j < units.size();++j) {
                        if (i == j) {
                            continue;
                        }
                        Game game = Game
                                .builder()
                                .gameId(sequenceGeneratorService.generateSequence(Game.SEQUENCE_NAME))
                                .status(GameStatus.NOT_START)
                                .unit0(units.get(i))
                                .unit1(units.get(j))
                                .build();
                        round.getGames().add(game.getGameId());
                        mongoTemplate.save(game);
                    }
                }
                break;
            case KNOCKOUT:
                // 复制一个新表
                List<String> us = new ArrayList<>(units);
                Random random = new Random(new Date().getTime());
                // 打乱顺序
                for (int i = 0;i < us.size();++i) {
                    String tmp = us.get(i);
                    int lucky = i + random.nextInt(us.size() - i);
                    us.set(i, us.get(lucky));
                    us.set(lucky, tmp);
                }
                if (us.size() % 2 == 1) {
                    Game game = Game
                            .builder()
                            .gameId(sequenceGeneratorService.generateSequence(Game.SEQUENCE_NAME))
                            .status(GameStatus.WIN_FIRST)
                            .unit0(us.remove(us.size() - 1))
                            .unit1(null)
                            .build();
                    round.getGames().add(game.getGameId());
                    mongoTemplate.save(game);
                }
                while (!us.isEmpty()) {
                    Game game = Game
                            .builder()
                            .gameId(sequenceGeneratorService.generateSequence(Game.SEQUENCE_NAME))
                            .status(GameStatus.NOT_START)
                            .unit0(us.remove(us.size() - 1))
                            .unit1(us.remove(us.size() - 1))
                            .build();
                    round.getGames().add(game.getGameId());
                    mongoTemplate.save(game);
                }
                break;
            case CUSTOM:
            default:
                break;
        }
    }
}
