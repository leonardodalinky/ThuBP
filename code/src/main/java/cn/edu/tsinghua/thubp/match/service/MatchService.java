package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.config.GlobalConfig;
import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.AutoModifyUtil;
import cn.edu.tsinghua.thubp.common.util.TimeUtil;
import cn.edu.tsinghua.thubp.match.entity.*;
import cn.edu.tsinghua.thubp.match.enums.MatchStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.match.misc.MatchMessageConstant;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import cn.edu.tsinghua.thubp.notification.service.NotificationService;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import cn.edu.tsinghua.thubp.plugin.exception.PluginErrorCode;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.graphql.misc.PagedMatchList;
import cn.edu.tsinghua.thubp.web.request.*;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import cn.edu.tsinghua.thubp.web.service.TokenGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    public static final String USERS = "users";
    public static final String MATCH_ID = "matchId";
    public static final String REFEREE_TOKEN = "refereeToken";
    public static final String MATCH_TYPE_ID = "matchTypeId";
    public static final String UNIT_ID = "unitId";
    public static final String ROUND_ID = "roundId";
    public static final String TOKEN = "token";
    public static final String UNITS = "units";
    public static final String UNIT0 = "unit0";
    public static final String UNIT1 = "unit1";
    public static final String REFEREES = "referees";
    public static final String STATUS = "status";
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
    public String createMatch(User user, MatchCreateRequest matchCreateRequest) throws MalformedURLException {
        // 检查赛事类型 ID 是否存在
        if (pluginRegistryService.getMatchType(matchCreateRequest.getMatchTypeId()) == null) {
            throw new CommonException(PluginErrorCode.MATCH_TYPE_NOT_FOUND,
                    ImmutableMap.of(MATCH_TYPE_ID, matchCreateRequest.getMatchTypeId()));
        }
        // 检验参赛单位最大最小人数是否可行
        if (matchCreateRequest.getMinUnitMember() > matchCreateRequest.getMaxUnitMember()) {
            throw new CommonException(MatchErrorCode.MATCH_UNIT_MIN_MAX_INVALID,
                    ImmutableMap.of("minUnitMember", matchCreateRequest.getMinUnitMember(),
                            "maxUnitMember", matchCreateRequest.getMaxUnitMember()));
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
                .minUnitMember(matchCreateRequest.getMinUnitMember())
                .maxUnitMember(matchCreateRequest.getMaxUnitMember())
                .matchTypeId(matchCreateRequest.getMatchTypeId())
                .comments(new ArrayList<>())
                .build();
        // 修改属性
        if (matchCreateRequest.getPreview() != null) {
            match.setPreview(
                    new URL(globalConfig.getQiNiuProtocol(), globalConfig.getQiNiuHost(), "/" + matchCreateRequest.getPreview())
            );
        }
        if (matchCreateRequest.getPreviewLarge() != null) {
            match.setPreviewLarge(
                    new URL(globalConfig.getQiNiuProtocol(), globalConfig.getQiNiuHost(), "/" + matchCreateRequest.getPreviewLarge())
            );
        }
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
        if (matchModifyRequest.getStatus() != null) {
            if (matchModifyRequest.getStatus().getOrder() < match.getStatus().getOrder()) {
                throw new CommonException(MatchErrorCode.MATCH_STATUS_ORDER,
                        ImmutableMap.of(MATCH_ID, matchId, STATUS, match.getStatus()));
            }
            match.setStatus(matchModifyRequest.getStatus());
        }
        if (matchModifyRequest.getPreview() != null) {
            match.setPreview(
                    new URL(globalConfig.getQiNiuProtocol(), globalConfig.getQiNiuHost(), "/" + matchModifyRequest.getPreview())
            );
        }
        if (matchModifyRequest.getPreviewLarge() != null) {
            match.setPreviewLarge(
                    new URL(globalConfig.getQiNiuProtocol(), globalConfig.getQiNiuHost(), "/" + matchModifyRequest.getPreviewLarge())
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
     * @param matchToken 查询者提供的 match token
     * @return 对应的赛事
     */
    public Match findByMatchId(@NotNull String matchId, boolean needPublicShow, @Nullable String userId, @Nullable String matchToken) {
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
                                    Criteria.where("referees").all(userId),
                                    Criteria.where("matchToken.token").is(matchToken)
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
    public List<Match> findMatchesByMatchIds(@NotNull List<String> matchIds, @Nullable Pageable pageable,
                                             boolean needPublicShow, @Nullable String userId) {
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
    public PagedMatchList findAllByMatchTypeIdIn(List<String> matchTypeIds, Pageable pageable,
                                                        boolean needPublicShow, @Nullable String userId) {
        Criteria criteria;
        if (!needPublicShow) {
            criteria = new Criteria().andOperator(
                    Criteria.where("matchTypeId").in(matchTypeIds)
            );

        } else {
            criteria = new Criteria().andOperator(
                    Criteria.where("matchTypeId").in(matchTypeIds),
                    new Criteria().orOperator(
                            Criteria.where("publicShowUp").is(true),
                            Criteria.where("organizerUserId").is(userId),
                            Criteria.where("participants").all(userId),
                            Criteria.where("referees").all(userId)
                    )
            );
        }
        Query query = Query.query(criteria).skip(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize());
        int totalSize = (int) mongoTemplate.count(query, Match.class);
        List<Match> result = mongoTemplate.find(query, Match.class);
        return PagedMatchList.builder()
                .page(pageable.getPageNumber())
                .pageSize(result.size())
                .totalSize(totalSize)
                .list(result)
                .build();
    }

    /**
     * 邀请用户成为裁判，给受邀请者发送私信.
     * @param sender 发送者
     * @param userIds 用户 ID 列表
     * @param matchId 赛事 ID
     * @return 成功发送出去的用户 ID 列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<String> sendRefereeInvitations(User sender, List<String> userIds, String matchId) {
        // 检验比赛存在
        Match match = mongoTemplate.findOne(Query.query(Criteria.where("matchId").is(matchId)), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        // 检查邀请码
        if (match.getRefereeToken() == null
                || match.getRefereeToken().getExpirationTime().toEpochMilli() < Instant.now().toEpochMilli()) {
            // 邀请码失效，重新生成一个
            match.setRefereeToken(assignRefereeToken(sender.getUserId(), matchId));
        }
        // 更新邀请码有效期
        match.getRefereeToken().setExpirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)));
        return notificationService.sendNotificationToMultipleUsers(userIds, SYSTEM_ID,
                MatchMessageConstant.INVITE_REFEREE_NOTIFICATION_TITLE
                        .replace("{inviter}", sender.getUsername())
                        .replace("{match}", match.getName()),
                MatchMessageConstant.INVITE_REFEREE_NOTIFICATION_CONTENT
                        .replace("{inviter}", sender.getUsername())
                        .replace("{match}", match.getName()),
                NotificationTag.REFEREE_INVITE,
                ImmutableMap.of("token", match.getRefereeToken().getToken(),
                        "expirationTime", match.getRefereeToken().getExpirationTime(),
                        "matchId", matchId)
        );
    }

    /**
     * 邀请用户成为裁判，给受邀请者发送私信.
     * @param sender 发送者
     * @param userIds 用户 ID 列表
     * @param matchId 赛事 ID
     * @return 成功发送出去的用户 ID 列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<String> sendMatchInvitations(User sender, List<String> userIds, String matchId) {
        // 检验比赛存在
        Match match = mongoTemplate.findOne(Query.query(Criteria.where("matchId").is(matchId)), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        // 检查邀请码
        if (match.getMatchToken() == null
                || match.getMatchToken().getExpirationTime().toEpochMilli() < Instant.now().toEpochMilli()) {
            // 邀请码失效，重新生成一个
            match.setMatchToken(assignMatchToken(sender.getUserId(), matchId));
        }
        // 更新邀请码有效期
        match.getMatchToken().setExpirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)));
        return notificationService.sendNotificationToMultipleUsers(userIds, SYSTEM_ID,
                MatchMessageConstant.INVITE_MATCH_NOTIFICATION_TITLE
                        .replace("{inviter}", sender.getUsername())
                        .replace("{match}", match.getName()),
                MatchMessageConstant.INVITE_MATCH_NOTIFICATION_CONTENT
                        .replace("{inviter}", sender.getUsername())
                        .replace("{match}", match.getName()),
                NotificationTag.MATCH_INVITE,
                ImmutableMap.of("token", match.getMatchToken().getToken(),
                        "expirationTime", match.getMatchToken().getExpirationTime(),
                        "matchId", matchId)
        );
    }

    /**
     * 签发一个裁判邀请码. 这会导致之前的邀请码失效.
     * @param userId 用户 ID
     * @param matchId 赛事 ID.
     * @return 成功签发的邀请码
     */
    @Transactional(rollbackFor = Exception.class)
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
                        Criteria.where("referees").not().all(userId),
                        Criteria.where("participants").not().all(userId)
                )),
                new Update().push("referees", userId), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED, ImmutableMap.of(MATCH_ID, matchId));
        }
    }

    /**
     * 删除裁判.
     * @param userId 用户 ID
     * @param matchId 赛事 ID
     * @param refereeDeleteRequest 删除裁判的请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void dropReferees(String userId, String matchId, RefereeDeleteRequest refereeDeleteRequest) {
        Match match = findByMatchId(matchId, false, null, null);
        // check status of match
        if (match.getStatus() != MatchStatus.PREPARE) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_PREPARE, ImmutableMap.of(MATCH_ID, matchId));
        }
        if (!match.getOrganizerUserId().equals(userId)) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, USER_ID, userId));
        }
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("organizerUserId").is(userId),
                        Criteria.where("referees").all(refereeDeleteRequest.getReferees())
                )),
                new Update().pullAll("referees", refereeDeleteRequest.getReferees().toArray()),
                Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_REFEREE_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, REFEREES, refereeDeleteRequest.getReferees())
            );
        }
        // 删除裁判参加的比赛
        long userUpdateCount = mongoTemplate.updateMulti(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").in(refereeDeleteRequest.getReferees()),
                        Criteria.where("participatedMatches").all(matchId)
                )),
                new Update().pull("participatedMatches", matchId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_PARTICIPANT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, USERS, refereeDeleteRequest.getReferees()));
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
     * 将 match 中的 user 列表中删去 members.
     * @param matchId matchId.
     * @param members 待删除的成员 ID 列表.
     */
    @Transactional(rollbackFor = Exception.class)
    public void dropParticipant(String matchId, List<String> members) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("participants").all(members)
                )),
                new Update().pullAll("participants", members.toArray()), Match.class).getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_PARTICIPANT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, USERS, members)
            );
        }
        long userUpdateCount = mongoTemplate.updateMulti(
                Query.query(new Criteria().andOperator(
                        Criteria.where("userId").in(members),
                        Criteria.where("participatedMatches").all(matchId)
                )),
                new Update().pull("participatedMatches", matchId), User.class).getModifiedCount();
        if (userUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.MATCH_PARTICIPANT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, USERS, members));
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
