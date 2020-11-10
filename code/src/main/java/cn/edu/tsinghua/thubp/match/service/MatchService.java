package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.TimeUtil;
import cn.edu.tsinghua.thubp.match.entity.*;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.match.enums.RoundGameStrategy;
import cn.edu.tsinghua.thubp.match.enums.RoundStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.match.repository.MatchRepository;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.*;
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
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchService {
    public static final String USER_ID = "userId";
    public static final String MATCH_ID = "matchId";
    public static final String REFEREE_TOKEN = "refereeToken";
    public static final String UNIT_ID = "unitId";
    public static final String ROUND_ID = "roundId";
    public static final String TOKEN = "token";
    public static final String UNITS = "units";
    public static final String UNIT0 = "unit0";
    public static final String UNIT1 = "unit1";
    public static final int TOKEN_LENGTH = 6;
    public static final int EXPIRATION_DAYS = 7;

    private final MatchRepository matchRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;
    private final TokenGeneratorService tokenGeneratorService;

    @Transactional(rollbackFor = Exception.class)
    public String createMatch(User user, MatchCreateRequest matchCreateRequest) {
        String matchId = sequenceGeneratorService.generateSequence(Match.SEQUENCE_NAME);
        Match match = Match.builder()
                .matchId(matchId)
                .organizerUserId(user.getUserId())
                .name(matchCreateRequest.getName())
                .description(matchCreateRequest.getDescription())
                .publicSignUp(matchCreateRequest.getPublicSignUp())
                .participants(new ArrayList<>())
                .referees(new ArrayList<>())
                .rounds(new ArrayList<>())
                .units(new ArrayList<>())
                .matchTypeId(matchCreateRequest.getMatchTypeId())
                .build();
        // 生成赛事邀请码
        if (matchCreateRequest.getPublicSignUp()) {
            String tokenStr = generateDistinctMatchToken();
            match.setMatchToken(
                    MatchToken
                    .builder()
                    .token(tokenStr)
                    .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                    .build()
            );
        }
        matchRepository.save(match);
        return matchId;
    }

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
        String roundId = sequenceGeneratorService.generateSequence(PRound.SEQUENCE_NAME);
        PRound round = PRound.builder()
                .roundId(roundId)
                .name(roundCreateRequest.getName())
                .description(roundCreateRequest.getDescription())
                .status(RoundStatus.NOT_START)
                .units(units)
                .games(new ArrayList<>())
                .build();
        roundAutoGenerateGame(round, roundCreateRequest.getAutoStrategy());
        mongoTemplate.save(round);

        return roundId;
    }

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
            )), PRound.class);
        } else {
            ret = mongoTemplate.exists(Query.query(new Criteria().andOperator(
                    Criteria.where("roundId").is(roundId),
                    Criteria.where("units").all(gameCreateRequest.getUnit0(), gameCreateRequest.getUnit1())
            )), PRound.class);
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
        ), new Update().push("units", game.getGameId()), PRound.class).getModifiedCount();
        if (cnt != 1) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
        return game.getGameId();
    }

    /**
     * 用户申请创建一个参赛单位
     * @param user 用户
     * @param matchId 赛事 ID
     * @param matchRegisterRequest 申请参赛单位的请求
     * @return 生成的参赛单位 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String registerIn(User user, String matchId, MatchRegisterRequest matchRegisterRequest) {
        // 排除 match 不存在的情况
        // 若赛事非公开报名且邀请码错误，同时也排除
        Match match = findByMatchId(matchId);
        if (!match.getPublicSignUp()) {
            if (match.getMatchToken() == null) {
                throw new CommonException(MatchErrorCode.MATCH_TOKEN_EXPIRED_OR_INVALID,
                        ImmutableMap.of("token", matchRegisterRequest.getToken()));
            } else if (!match.getMatchToken().valid(matchRegisterRequest.getToken())) {
                throw match.getMatchToken().createException(matchRegisterRequest.getToken());
            }
        }
        // 排除重复报名
        List<String> matches = user.getParticipatedMatches();
        if (matches != null && matches.contains(matchId)) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
        matchAddParticipant(user.getUserId(), matchId);
        // 创建新的 PUnit，并且添加到数据库
        PUnit pUnit = createUnit(user, matchId, matchRegisterRequest);
        return pUnit.getUnitId();
    }

    /**
     * 加入一个参赛单位
     * @param user 用户
     * @param unitId 参赛单位 ID
     * @param unitParticipateRequest 加入参赛单位的请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void participateIn(User user, String unitId, UnitParticipateRequest unitParticipateRequest) {
        // 排除 unit 不存在的情况
        // 若邀请码错误，同时也排除
        PUnit unit = mongoTemplate.findOne(Query.query(
                Criteria.where("unitId").is(unitId)
        ), PUnit.class);
        if (unit == null) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(UNIT_ID, unitId));
        } else if (unit.getUnitToken() == null || !unit.getUnitToken().valid(unitParticipateRequest.getToken())) {
            throw new CommonException(MatchErrorCode.UNIT_TOKEN_EXPIRED_OR_INVALID,
                    ImmutableMap.of(TOKEN, unitParticipateRequest.getToken()));
        }
        // 排除重复报名
        List<String> matches = user.getParticipatedMatches();
        if (matches != null && matches.contains(unit.getMatchId())) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, unit.getMatchId()));
        } else if (unit.getMembers().contains(user.getUserId())) {
            throw new CommonException(MatchErrorCode.UNIT_ALREADY_PARTICIPATED,
                    ImmutableMap.of(UNIT_ID, unitId, USER_ID, user.getUserId()));
        }
        matchAddParticipant(user.getUserId(), unit.getMatchId());
        unitAddMember(user.getUserId(), unitId);
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
        ), PRound.class);
        if (!ret) {
            throw new CommonException(MatchErrorCode.ROUND_UNIT_INVALID, ImmutableMap.of(UNITS, gameDeleteRequest.getGames()));
        }
        // 删除
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("roundId").is(roundId)
        ), new Update().pullAll("units", gameDeleteRequest.getGames().toArray()), PRound.class).getModifiedCount();
        if (cnt == 0) {
            throw new CommonException(MatchErrorCode.ROUND_NOT_FOUND, ImmutableMap.of(ROUND_ID, roundId));
        }
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
     * @param userId 用户 ID
     * @param matchId 赛事 ID.
     * @return 成功签发的邀请码
     */
    public RefereeToken assignRefereeToken(String userId, String matchId) {
        // 生成一个目前唯一未过期的邀请码
        String tokenStr =  generateDistinctRefereeToken();
        // 生成邀请码
        RefereeToken token = RefereeToken.builder().token(tokenStr)
                .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                .build();
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
     * 签发一个参赛单位邀请码. 这会导致之前的邀请码失效.
     * @param userId 用户 ID
     * @param unitId 参赛单位 ID
     * @return 成功签发的邀请码
     */
    public PUnitToken assignUnitToken(String userId, String unitId) {
        // 生成一个目前唯一未过期的邀请码
        String tokenStr =  generateDistinctUnitToken();
        // 生成邀请码
        PUnitToken token = PUnitToken.builder().token(tokenStr)
                .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                .build();
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("creatorId").is(userId)
                )),
                new Update().set("unitToken", token), PUnit.class)
                .getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(UNIT_ID, unitId));
        }
        return token;
    }

    /**
     * 使用裁判邀请码成为裁判.
     * @param matchId 赛事 ID.
     * @param userId 用户 ID.
     */
    public void becomeRefereeByToken(String userId, String matchId, String token) {
        // 检验比赛存在
        if (!mongoTemplate.exists(Query.query(Criteria.where("matchId").is(matchId)), Match.class)) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        // 检查邀请码
        List<Match> matches = mongoTemplate.find(
                Query.query(new Criteria().andOperator(
                        Criteria.where("refereeToken.token").is(token),
                        Criteria.where("refereeToken.expirationTime").gt(Instant.now())
                )), Match.class);
        if (matches.size() != 1) {
            throw new CommonException(MatchErrorCode.MATCH_REFEREE_TOKEN_EXPIRED_OR_INVALID, ImmutableMap.of(REFEREE_TOKEN, token));
        }
        // 加入
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

    /**
     * 创建 PUnit
     * @param user
     * @param matchId
     * @param matchRegisterRequest
     * @return
     */
    private PUnit createUnit(User user, String matchId, MatchRegisterRequest matchRegisterRequest) {
        PUnit pUnit = PUnit.builder()
                .unitId(sequenceGeneratorService.generateSequence(PUnit.SEQUENCE_NAME))
                .unitName(matchRegisterRequest.getUnitName())
                .matchId(matchId)
                .creatorId(user.getUserId())
                .members(new ArrayList<>())
                .build();
        mongoTemplate.save(pUnit);
        mongoTemplate.updateFirst(Query.query(
                Criteria.where("matchId").is(matchId)),
                new Update().push("units", pUnit.getUnitId()),
                Match.class
        );
        unitAddMember(user.getUserId(), pUnit.getUnitId());
        return pUnit;
    }

    /**
     * 将 userId 加入到 match 中的 user 列表中
     * @param userId userId
     * @param matchId matchId
     */
    private void matchAddParticipant(String userId, String matchId) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("participants").all(userId)
                )),
                new Update().push("participants", userId), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
        long userUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("participatedMatches").all(matchId)
                )),
                new Update().push("participatedMatches", matchId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
    }

    /**
     * 将用户和参赛单位关联
     * @param userId 用户 ID
     * @param unitId 参赛单位 ID
     */
    private void unitAddMember(String userId, String unitId) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("members").all(userId)
                )),
                new Update().push("members", userId), PUnit.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.UNIT_ALREADY_PARTICIPATED, ImmutableMap.of(UNIT_ID, unitId));
        }
        long userUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("participatedUnits").not().all(unitId)
                )),
                new Update().push("participatedUnits", unitId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(UNIT_ID, unitId));
        }
    }

    /**
     * 给 round 根据 strategy 自动加入 Game
     * 不检测 round 中已存在的 game
     * @param round Round 对象
     * @param strategy 策略
     */
    private void roundAutoGenerateGame(PRound round, RoundGameStrategy strategy) {
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

    /**
     * 生成唯一的赛事邀请码
     * @return 当前时期内唯一的赛事邀请码
     */
    private String generateDistinctMatchToken() {
        // 生成一个目前唯一未过期的邀请码
        // TODO: 若赛事从未公开转为公开，则赛事的邀请码需要作废
        String tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
        while (true) {
            boolean rc = mongoTemplate.exists(
                    Query.query(new Criteria().andOperator(
                            Criteria.where("publicSignUp").is(false),
                            Criteria.where("matchToken.token").is(tokenStr),
                            Criteria.where("matchToken.expirationTime").gt(Instant.now())
                    )), Match.class);
            if (rc) {
                tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
            } else {
                break;
            }
        }
        return tokenStr;
    }

    /**
     * 生成唯一的裁判邀请码
     * @return 当前时期内唯一的裁判邀请码
     */
    private String generateDistinctRefereeToken() {
        // 生成一个目前唯一未过期的邀请码
        String tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
        while (true) {
            boolean rc = mongoTemplate.exists(
                    Query.query(new Criteria().andOperator(
                            Criteria.where("refereeToken.token").is(tokenStr),
                            Criteria.where("refereeToken.expirationTime").gt(Instant.now())
                    )), Match.class);
            if (rc) {
                tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
            } else {
                break;
            }
        }
        return tokenStr;
    }

    /**
     * 生成唯一的参赛单位邀请码
     * @return 当前时期内唯一的参赛单位邀请码
     */
    private String generateDistinctUnitToken() {
        // 生成一个目前唯一未过期的邀请码
        String tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
        while (true) {
            boolean rc = mongoTemplate.exists(
                    Query.query(new Criteria().andOperator(
                            Criteria.where("unitToken.token").is(tokenStr),
                            Criteria.where("unitToken.expirationTime").gt(Instant.now())
                    )), Match.class);
            if (rc) {
                tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
            } else {
                break;
            }
        }
        return tokenStr;
    }

}
