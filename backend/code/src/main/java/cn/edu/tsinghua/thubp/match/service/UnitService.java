package cn.edu.tsinghua.thubp.match.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.AutoModifyUtil;
import cn.edu.tsinghua.thubp.common.util.TimeUtil;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.entity.UnitToken;
import cn.edu.tsinghua.thubp.match.enums.MatchStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.match.misc.MatchMessageConstant;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import cn.edu.tsinghua.thubp.notification.service.NotificationService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.exception.UserErrorCode;
import cn.edu.tsinghua.thubp.web.request.MatchRegisterRequest;
import cn.edu.tsinghua.thubp.web.request.UnitModifyRequest;
import cn.edu.tsinghua.thubp.web.request.UnitParticipateRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import cn.edu.tsinghua.thubp.web.service.TokenGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UnitService {
    public static final String USER_ID = "userId";
    public static final String USERS = "users";
    public static final String MATCH_ID = "matchId";
    public static final String REFEREE_TOKEN = "refereeToken";
    public static final String UNIT_ID = "unitId";
    public static final String ROUND_ID = "roundId";
    public static final String TOKEN = "token";
    public static final String UNITS = "units";
    public static final String UNIT0 = "unit0";
    public static final String UNIT1 = "unit1";
    public static final String STATUS = "status";
    public static final int TOKEN_LENGTH = 6;
    public static final int EXPIRATION_DAYS = 7;

    // 依赖于 matchService
    private final MatchService matchService;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final MongoTemplate mongoTemplate;
    private final TokenGeneratorService tokenGeneratorService;
    private final NotificationService notificationService;

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
        // 只允许在 PREPARE 阶段
        Match match = mongoTemplate.findOne(Query.query(
                new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("active").is(true)
                )
        ), Match.class);
        if (match == null) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId));
        }
        if (match.getStatus() != MatchStatus.PREPARE) {
            throw new CommonException(MatchErrorCode.MATCH_STATUS_ORDER,
                    ImmutableMap.of(MATCH_ID, matchId, STATUS, match.getStatus()));
        }
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
        matchService.addParticipant(user.getUserId(), matchId);
        // 创建新的 Unit，并且添加到数据库
        Unit unit = createUnit(user, matchId, matchRegisterRequest);
        // 分配一个新邀请码
        assignUnitToken(user.getUserId(), unit.getUnitId());
        return unit.getUnitId();
    }

    /**
     * 加入一个参赛单位.
     *
     * 如果目前人数大于
     * @param user 用户
     * @param unitId 参赛单位 ID
     * @param unitParticipateRequest 加入参赛单位的请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void participateIn(User user, String unitId, UnitParticipateRequest unitParticipateRequest) {
        // 排除 unit 不存在的情况
        // 若邀请码错误，同时也排除
        Unit unit = mongoTemplate.findOne(Query.query(
                Criteria.where("unitId").is(unitId)
        ), Unit.class);
        if (unit == null) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND,
                    ImmutableMap.of(UNIT_ID, unitId, UNIT_ID, unitId));
        } else if (unit.getUnitToken() == null || !unit.getUnitToken().valid(unitParticipateRequest.getToken())) {
            throw new CommonException(MatchErrorCode.UNIT_TOKEN_EXPIRED_OR_INVALID,
                    ImmutableMap.of(TOKEN, unitParticipateRequest.getToken()));
        }
        // 排除重复报名
        List<String> matches = user.getParticipatedMatches();
        if (matches != null && matches.contains(unit.getMatchId())) {
            throw new CommonException(MatchErrorCode.MATCH_ALREADY_PARTICIPATED,
                    ImmutableMap.of(MATCH_ID, unit.getMatchId(), UNIT_ID, unitId));
        } else if (unit.getMembers().contains(user.getUserId())) {
            throw new CommonException(MatchErrorCode.UNIT_ALREADY_PARTICIPATED,
                    ImmutableMap.of(UNIT_ID, unitId, USER_ID, user.getUserId()));
        }
        // 排除已达到最大人数的问题
        Match match = matchService.findByMatchId(unit.getMatchId(), false, null, null);
        if (unit.getMembers().size() >= match.getMaxUnitMember()) {
            throw new CommonException(MatchErrorCode.UNIT_MAX_MEMBER,
                    ImmutableMap.of("maxUnitMember", match.getMaxUnitMember()));
        }
        matchService.addParticipant(user.getUserId(), unit.getMatchId());
        addMember(user.getUserId(), unitId);
        // 发送系统通知
        notificationService.sendNotificationFromSystem(
                user.getUserId(),
                "参与小组成功!",
                String.format("您已参加小组(unitId: %s)，谢谢您的参加!", unitId),
                NotificationTag.NORMAL,
                null);
    }

    public Unit infoUnit(String userId, String matchId, String unitId) {
        // 校验
        Unit unit = mongoTemplate.findOne(Query.query(
                new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("creatorId").is(userId)
                )
        ), Unit.class);
        if (unit == null) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId));
        }
        return unit;
    }

    /**
     * 修改参赛单位信息
     * @param userId 用户 ID
     * @param matchId 赛事 ID
     * @param unitId 参赛单位 ID
     * @param unitModifyRequest 修改参赛单位信息的请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyUnit(String userId, String matchId, String unitId, UnitModifyRequest unitModifyRequest) {
        // 校验
        Unit unit = mongoTemplate.findOne(Query.query(
                new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("creatorId").is(userId)
                )
        ), Unit.class);
        if (unit == null) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId));
        }
        // 修改
        AutoModifyUtil.autoModify(unitModifyRequest, unit);
        mongoTemplate.save(unit);
    }

    /**
     * 删除小队成员，不能包括自身.
     * @param userId 用户 ID.
     * @param matchId 赛事 ID.
     * @param unitId 参赛单位 ID.
     * @param members 待删除的成员.
     * @param checkStatus 删除时是否检查赛事状态.
     */
    @Transactional
    public void deleteUnitMembers(String userId, String matchId, String unitId, List<String> members, boolean checkStatus) {
        // 不可删除自身.
        if (members.contains(userId)) {
            // could not delete itself
            throw new CommonException(MatchErrorCode.UNIT_DELETE_SELF, ImmutableMap.of(USER_ID, userId));
        }
        // 校验
        Unit unit = mongoTemplate.findOne(Query.query(
                new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("creatorId").is(userId)
                )
        ), Unit.class);
        if (unit == null) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId));
        }
        // 不可删除多余的人
        if (!unit.getMembers().containsAll(members)) {
            throw new CommonException(MatchErrorCode.UNIT_DELETE_NOT_FOUND, ImmutableMap.of(USERS, members));
        }
        // 获得比赛
        Match match = matchService.findByMatchId(matchId, false, null, null);
        // check status of match
        if (checkStatus && match.getStatus() != MatchStatus.PREPARE) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_PREPARE, ImmutableMap.of(MATCH_ID, matchId));
        }
        // check permission
        if (!unit.getCreatorId().equals(userId) && !match.getOrganizerUserId().equals(userId)) {
            throw new CommonException(MatchErrorCode.UNIT_PERMISSION_DENIED,
                    ImmutableMap.of(UNIT_ID, unitId, USER_ID, userId));
        }
        // 删除 match 中的 unit 成员
        if (!match.getParticipants().removeAll(members)) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId, USERS, members));
        }
        mongoTemplate.save(match);
        // for now, ignore the return value
        // 清除每个成员的信息
        mongoTemplate.updateMulti(Query.query(
                Criteria.where("userId").in(members)
        ), new Update().pull("participatedMatches", matchId).pull("participatedUnits", unitId), User.class);
        // 清除 unit 中的成员
        if (!unit.getMembers().removeAll(members)) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId, USERS, members));
        }
        mongoTemplate.save(unit);
    }

    /**
     * 解散参赛单位，仅组长可用，且须在赛事开始前.
     * @param userId 用户 ID.
     * @param matchId 赛事 ID.
     * @param unitId 参赛单位 ID.
     * @param checkStatus 删除时是否检查赛事状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUnit(String userId, String matchId, String unitId, boolean checkStatus) {
        Match match = matchService.findByMatchId(matchId, false, null, null);
        // check status of match
        if (checkStatus && match.getStatus() != MatchStatus.PREPARE) {
            throw new CommonException(MatchErrorCode.MATCH_NOT_PREPARE, ImmutableMap.of(MATCH_ID, matchId));
        }
        // delete unit
        Unit unit = findByUnitId(unitId);
        // check permission
        if (!unit.getCreatorId().equals(userId) && !match.getOrganizerUserId().equals(userId)) {
            throw new CommonException(MatchErrorCode.UNIT_PERMISSION_DENIED,
                    ImmutableMap.of(UNIT_ID, unitId, USER_ID, userId));
        }
        // 删除 match 中的 unit
        if (!match.getUnits().remove(unitId)) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId));
        }
        // 删除 match 中的 unit 成员
        if (!match.getParticipants().removeAll(unit.getMembers())) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND,
                    ImmutableMap.of(MATCH_ID, matchId, UNIT_ID, unitId, USERS, unit.getMembers()));
        }
        // for now, ignore the return value
        mongoTemplate.updateMulti(Query.query(
                Criteria.where("userId").in(unit.getMembers())
        ), new Update().pull("participatedMatches", matchId).pull("participatedUnits", unitId), User.class);
        mongoTemplate.save(match);
        mongoTemplate.remove(unit);
    }

    /**
     * 签发一个参赛单位邀请码. 这会导致之前的邀请码失效.
     * @param userId 用户 ID
     * @param unitId 参赛单位 ID
     * @return 成功签发的邀请码
     */
    @Transactional(rollbackFor = Exception.class)
    public UnitToken assignUnitToken(String userId, String unitId) {
        // 生成一个目前唯一未过期的邀请码
        String tokenStr =  generateDistinctUnitToken();
        // 生成邀请码
        UnitToken token = UnitToken.builder().token(tokenStr)
                .expirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)))
                .build();
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("creatorId").is(userId)
                )),
                new Update().set("unitToken", token), Unit.class)
                .getModifiedCount();
        if (matchUpdateCount == 0) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(UNIT_ID, unitId));
        }
        return token;
    }

    /**
     * 向用户发送邀请请求.
     *
     * 需要邀请码已经生成，否则返回错误。
     * @param user 用户
     * @param unitId 参赛单位 ID
     * @param invitedUserIds 被邀请的用户 ID
     */
    public void inviteMembers(User user, String unitId, List<String> invitedUserIds) {
        Unit unit = findByUnitId(unitId);
        // 检验参赛单位的操作权限
        if (!unit.getCreatorId().equals(user.getUserId())) {
            throw new CommonException(MatchErrorCode.UNIT_PERMISSION_DENIED,
                    ImmutableMap.of(UNIT_ID, unitId, USER_ID, user.getUserId()));
        }
        // 检验验证码是否产生
        if (unit.getUnitToken() == null || unit.getUnitToken().isExpired()) {
            assignUnitToken(user.getUserId(), unitId);
        }
        // 更新有效期
        unit.getUnitToken().setExpirationTime(Instant.ofEpochMilli(TimeUtil.getFutureTimeMillisByDays(EXPIRATION_DAYS)));
        Match match = matchService.findByMatchId(unit.getMatchId(), false, null, null);
        notificationService.sendNotificationToMultipleUsers(
                invitedUserIds,
                user.getUserId(),
                MatchMessageConstant.INVITE_UNIT_NOTIFICATION_TITLE
                        .replace("{inviter}", user.getUsername())
                        .replace("{match}", match.getName())
                        .replace("{unit}", unit.getName()),
                MatchMessageConstant.INVITE_UNIT_NOTIFICATION_CONTENT
                        .replace("{inviter}", user.getUsername())
                        .replace("{match}", match.getName())
                        .replace("{unit}", unit.getName()),
                NotificationTag.UNIT_INVITE,
                ImmutableMap.of("token", unit.getUnitToken().getToken(),
                        "expirationTime", unit.getUnitToken().getExpirationTime(),
                        "matchId", unit.getMatchId(),
                        "unitId", unitId)
        );
    }

    /**
     * 找寻一个 unit
     * @param unitId unit id
     * @return Unit
     */
    public Unit findByUnitId(@NotNull String unitId) {
        Unit ret = mongoTemplate.findOne(Query.query(
                Criteria.where("unitId").is(unitId)
        ), Unit.class);
        if (ret == null) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(UNIT_ID, unitId));
        }
        return ret;
    }

    /**
     * 通过 unitId 的列表，找到所对应的所有 Unit
     * @param unitIds unitId 的列表
     * @return Unit 列表
     */
    public List<Unit> findByUnitIds(@NotNull List<String> unitIds) {
        List<Unit> ret = mongoTemplate.find(Query.query(
                Criteria.where("unitId").in(unitIds)
        ), Unit.class);
        if (ret.size() != unitIds.size()) {
            throw new CommonException(MatchErrorCode.UNIT_NOT_FOUND, ImmutableMap.of(UNITS, unitIds));
        }
        return ret;
    }

    /**
     * 创建 Unit
     * @param user 用户
     * @param matchId 赛事 id
     * @param matchRegisterRequest 赛事注册请求
     * @return 创建出的参赛单位
     */
    private Unit createUnit(User user, String matchId, MatchRegisterRequest matchRegisterRequest) {
        Unit unit = Unit.builder()
                .unitId(sequenceGeneratorService.generateSequence(Unit.SEQUENCE_NAME))
                .name(matchRegisterRequest.getUnitName())
                .description(matchRegisterRequest.getDescription())
                .matchId(matchId)
                .creatorId(user.getUserId())
                .members(new ArrayList<>())
                .build();
        mongoTemplate.save(unit);
        mongoTemplate.updateFirst(Query.query(
                new Criteria().andOperator(
                        Criteria.where("matchId").is(matchId),
                        Criteria.where("active").is(true)
                )),
                new Update().push("units", unit.getUnitId()),
                Match.class
        );
        addMember(user.getUserId(), unit.getUnitId());
        return unit;
    }

    /**
     * 将用户和参赛单位关联
     * @param userId 用户 ID
     * @param unitId 参赛单位 ID
     */
    private void addMember(String userId, String unitId) {
        long matchUpdateCount = mongoTemplate.updateFirst(
                Query.query(new Criteria().andOperator(
                        Criteria.where("unitId").is(unitId),
                        Criteria.where("members").not().all(userId)
                )),
                new Update().push("members", userId), Unit.class).getModifiedCount();
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
     * 生成唯一的参赛单位邀请码
     * @return 当前时期内唯一的参赛单位邀请码
     */
    private String generateDistinctUnitToken() {
        // 生成一个目前唯一未过期的邀请码
        String tokenStr = tokenGeneratorService.generateToken(TOKEN_LENGTH);
        while (true) {
            boolean rc = mongoTemplate.exists(
                    Query.query(new Criteria().andOperator(
                            Criteria.where("active").is(true),
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
