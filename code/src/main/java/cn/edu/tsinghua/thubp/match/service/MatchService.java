package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.config.GlobalConfig;
import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.AutoModifyUtil;
import cn.edu.tsinghua.thubp.common.util.TimeUtil;
import cn.edu.tsinghua.thubp.match.entity.*;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.match.misc.MatchMessageConstant;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import cn.edu.tsinghua.thubp.notification.service.NotificationService;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import cn.edu.tsinghua.thubp.plugin.exception.PluginErrorCode;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.*;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import cn.edu.tsinghua.thubp.web.service.TokenGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

import static cn.edu.tsinghua.thubp.notification.service.NotificationService.SYSTEM_ID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchService {
    public static final String USER_ID = "userId";
    public static final String MATCH_ID = "matchId";
    public static final String REFEREE_TOKEN = "refereeToken";
    public static final String MATCH_TYPE_ID = "matchTypeId";
    public static final String UNIT_ID = "unitId";
    public static final String ROUND_ID = "roundId";
    public static final String TOKEN = "token";
    public static final String UNITS = "units";
    public static final String UNIT0 = "unit0";
    public static final String UNIT1 = "unit1";
    public static final int TOKEN_LENGTH = 6;
    public static final int EXPIRATION_DAYS = 7;

    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;
    private final TokenGeneratorService tokenGeneratorService;
    private final GlobalConfig globalConfig;
    private final PluginRegistryService pluginRegistryService;
    private final NotificationService notificationService;

    /**
     * 组织者用户获取比赛，否则抛出 Exception
     * @param userId 用户 ID
     * @param matchId 赛事 ID
     * @return 赛事
     */
    public Match infoMatch(String userId, String matchId) {
        Match match = mongoTemplate.findOne(Query.query(
                new Criteria().andOperator(
                        Criteria.where("active").is(true),
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("organizerUserId").is(userId)
                )
        ), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        return match;
    }

    /**
     * 创建赛事
     * @param user 用户
     * @param matchCreateRequest 赛事创建请求
     * @return 新的赛事的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String createMatch(User user, MatchCreateRequest matchCreateRequest) {
        // 检查赛事类型 ID 是否存在
        if (pluginRegistryService.getMatchType(matchCreateRequest.getMatchTypeId()) == null) {
            throw new CommonException(PluginErrorCode.MATCH_TYPE_NOT_FOUND,
                    ImmutableMap.of(MATCH_TYPE_ID, matchCreateRequest.getMatchTypeId()));
        }
        String matchId = sequenceGeneratorService.generateSequence(Match.SEQUENCE_NAME);
        Match match = Match.builder()
                .matchId(matchId)
                .active(true)
                .organizerUserId(user.getUserId())
                .name(matchCreateRequest.getName())
                .description(matchCreateRequest.getDescription())
                .publicShowUp(matchCreateRequest.getPublicShowUp())
                .targetGroup(matchCreateRequest.getTargetGroup())
                .startTime(matchCreateRequest.getStartTime())
                .publicSignUp(matchCreateRequest.getPublicSignUp())
                .participants(new ArrayList<>())
                .referees(new ArrayList<>())
                .rounds(new ArrayList<>())
                .units(new ArrayList<>())
                .matchTypeId(matchCreateRequest.getMatchTypeId())
                .comments(new ArrayList<>())
                .build();
        // 生成赛事邀请码
        if (!matchCreateRequest.getPublicSignUp()) {
            String tokenStr = generateDistinctMatchToken();
            match.setMatchToken(
                    MatchToken
                    .builder()
                    .token(tokenStr)
                    .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                    .build()
            );
        }
        mongoTemplate.save(match);
        if (user.getOrganizedMatches() == null) {
            user.setOrganizedMatches(new ArrayList<>());
        }
        user.getOrganizedMatches().add(matchId);
        mongoTemplate.save(user);
        return matchId;
    }

    /**
     * 修改赛事信息
     * @param userId 用户 ID
     * @param matchId 赛事 ID
     * @param matchModifyRequest 赛事信息修改的请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyMatch(String userId, String matchId, MatchModifyRequest matchModifyRequest)
            throws MalformedURLException {
        // 校验
        Match match = mongoTemplate.findOne(Query.query(
                new Criteria().andOperator(
                        Criteria.where("active").is(true),
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("organizerUserId").is(userId)
                )
        ), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        // 自动修改部分属性
        AutoModifyUtil.autoModify(matchModifyRequest, match);
        // 修改属性
        if (matchModifyRequest.getPreview() != null) {
            match.setPreview(
                    new URL(globalConfig.getQiNiuProtocol(), globalConfig.getQiNiuHost(), matchModifyRequest.getPreview())
            );
        }
        if (matchModifyRequest.getPreviewLarge() != null) {
            match.setPreviewLarge(
                    new URL(globalConfig.getQiNiuProtocol(), globalConfig.getQiNiuHost(), matchModifyRequest.getPreviewLarge())
            );
        }
        if (matchModifyRequest.getPublicSignUp() != null && matchModifyRequest.getPublicSignUp() != match.getPublicSignUp()) {
            if (!matchModifyRequest.getPublicSignUp()) {
                // 重新生成赛事邀请码
                String tokenStr = generateDistinctMatchToken();
                match.setMatchToken(
                        MatchToken
                                .builder()
                                .token(tokenStr)
                                .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                                .build()
                );
            } else {
                match.setMatchToken(null);
            }

            match.setPublicSignUp(matchModifyRequest.getPublicSignUp());
        }
        // 存储
        mongoTemplate.save(match);
    }

    /**
     * 根据 matchId 查找赛事.
     * @param matchId 赛事 ID
     * @param needPublicShow 需要赛事是可公开查看的，否则只有关系者能看
     * @param userId 查询者 ID，可为空
     * @return 对应的赛事
     */
    public Match findByMatchId(String matchId, boolean needPublicShow, @Nullable String userId) {
        Match match;
        if (!needPublicShow) {
            match = mongoTemplate.findOne(Query.query(
                    new Criteria().andOperator(
                            Criteria.where("matchId").is(matchId)
                    )
            ), Match.class);
        } else {
            match = mongoTemplate.findOne(Query.query(
                    new Criteria().andOperator(
                            Criteria.where("matchId").is(matchId),
                            new Criteria().orOperator(
                                    Criteria.where("publicShowUp").is(true),
                                    Criteria.where("organizerUserId").is(userId),
                                    Criteria.where("participants").all(userId),
                                    Criteria.where("referees").all(userId)
                            )
                    )
            ), Match.class);
        }

        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        return match;
    }

    /**
     * 根据 matchId 列表查找赛事列表.
     * @param matchIds matchId 列表
     * @param pageable 分页选项，{@code null} 表示不分页
     * @param needPublicShow 需要赛事是可公开查看的，否则只有关系者能看
     * @param userId 查询者 ID，可为空，配合 {@code needPublicShow} 使用
     * @return 对应的赛事列表
     */
    public List<Match> findMatchesByMatchIds(List<String> matchIds, @Nullable Pageable pageable,
                                             boolean needPublicShow, @Nullable String userId) {
        Match match;
        if (!needPublicShow) {
            if (pageable != null) {
                return mongoTemplate.find(Query.query(
                        new Criteria().andOperator(
                                Criteria.where("matchId").in(matchIds)
                        )).skip(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize()),
                        Match.class);
            } else {
                return mongoTemplate.find(Query.query(
                        new Criteria().andOperator(
                                Criteria.where("matchId").in(matchIds)
                        )), Match.class);
            }
        } else {
            if (pageable != null) {
                return mongoTemplate.find(Query.query(
                        new Criteria().andOperator(
                                Criteria.where("matchId").in(matchIds),
                                Criteria.where("active").is(true),
                                new Criteria().orOperator(
                                        Criteria.where("publicShowUp").is(true),
                                        Criteria.where("organizerUserId").is(userId),
                                        Criteria.where("participants").all(userId),
                                        Criteria.where("referees").all(userId)
                                )
                        )).skip(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize()),
                        Match.class);
            } else {
                return mongoTemplate.find(Query.query(
                        new Criteria().andOperator(
                                Criteria.where("matchId").in(matchIds),
                                Criteria.where("active").is(true),
                                new Criteria().orOperator(
                                        Criteria.where("publicShowUp").is(true),
                                        Criteria.where("organizerUserId").is(userId),
                                        Criteria.where("participants").all(userId),
                                        Criteria.where("referees").all(userId)
                                )
                        )
                ), Match.class);
            }
        }
    }

    /**
     * 查找某些类型的赛事
     * @param matchTypeIds 类型 Id 列表
     * @param pageable 分页选项
     * @param needPublicShow 需要赛事是可公开查看的，否则只有关系者能看
     * @param userId 查询者 ID，可为空，配合 {@code needPublicShow} 使用
     * @return 查询到的比赛
     */
    public List<Match> findAllByMatchTypeIdIn(List<String> matchTypeIds, Pageable pageable,
                                              boolean needPublicShow, @Nullable String userId) {
        if (!needPublicShow) {
            return mongoTemplate.find(Query.query(
                    new Criteria().andOperator(
                            Criteria.where("matchTypeId").in(matchTypeIds)
                    )).skip(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize()),
                    Match.class);
        } else {
            return mongoTemplate.find(Query.query(
                    new Criteria().andOperator(
                            Criteria.where("matchTypeId").in(matchTypeIds),
                            new Criteria().orOperator(
                                    Criteria.where("publicShowUp").is(true),
                                    Criteria.where("organizerUserId").is(userId),
                                    Criteria.where("participants").all(userId),
                                    Criteria.where("referees").all(userId)
                            )
                    )).skip(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize()),
                    Match.class);
        }
    }

    /**
     * 邀请用户成为裁判，给受邀请者发送私信.
     * @param senderName 发送者用户名
     * @param userIds 用户 ID 列表
     * @param matchId 赛事 ID
     * @return 成功发送出去的用户 ID 列表
     */
    public List<String> sendRefereeInvitations(String senderName, List<String> userIds, String matchId) {
        // 检验比赛存在
        Match match = mongoTemplate.findOne(Query.query(Criteria.where("matchId").is(matchId)), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        // 检查邀请码
        if (match.getRefereeToken() == null
                || match.getRefereeToken().getExpirationTime().toEpochMilli() < Instant.now().toEpochMilli()) {
            throw new CommonException(MatchErrorCode.MATCH_REFEREE_TOKEN_EXPIRED_OR_INVALID,
                    ImmutableMap.of(MATCH_ID, match.getMatchTypeId()));
        }
        return notificationService.sendNotificationToMultipleUsers(userIds, SYSTEM_ID,
                MatchMessageConstant.INVITE_REFEREE_NOTIFICATION_TITLE
                        .replace("{inviter}", senderName)
                        .replace("{match}", match.getName()),
                MatchMessageConstant.INVITE_REFEREE_NOTIFICATION_CONTENT.replace("{inviter}", senderName)
                        .replace("{match}", match.getName())
                        .replace("{refereeToken}", match.getRefereeToken().getToken()),
                NotificationTag.REFEREE_INVITE);
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
     * 签发一个加入赛事. 这会导致之前的邀请码失效.
     * @param userId 用户 ID
     * @param matchId 赛事 ID.
     * @return 成功签发的邀请码
     */
    @Transactional(rollbackFor = Exception.class)
    public MatchToken assignMatchToken(String userId, String matchId) {
        // 检验比赛是否公开
        boolean ret = mongoTemplate.exists(Query.query(
                new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("publicSignUp").is(true)
                )
        ), Match.class);
        if (ret) {
            throw new CommonException(MatchErrorCode.MATCH_PUBLIC, ImmutableMap.of(MATCH_ID, matchId));
        }
        // 生成一个目前唯一未过期的邀请码
        String tokenStr =  generateDistinctMatchToken();
        // 生成邀请码
        MatchToken token = MatchToken.builder().token(tokenStr)
                .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                .build();
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("organizerUserId").is(userId)
                )),
                new Update().set("matchToken", token), Match.class)
                .getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        return token;
    }

    /**
     * 将 userId 加入到 match 中的 user 列表中
     * @param userId userId
     * @param matchId matchId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addParticipant(String userId, String matchId) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("participants").not().all(userId)
                )),
                new Update().push("participants", userId), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
        long userUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("participatedMatches").not().all(matchId)
                )),
                new Update().push("participatedMatches", matchId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
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
                            Criteria.where("active").is(true),
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
                            Criteria.where("active").is(true),
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

}
