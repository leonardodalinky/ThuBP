package cn.edu.tsinghua.thubp.schedule.timer;

import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import cn.edu.tsinghua.thubp.notification.service.NotificationService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenTimer {
    private final MongoTemplate mongoTemplate;
    private final NotificationService notificationService;

    public static final String REFEREE_EXPIRING_TITLE = "赛事 {match} 裁判邀请码即将过期";
    public static final String REFEREE_EXPIRING_CONTENT = "赛事 {match}(ID: {matchId}) 裁判邀请码即将过期";
    public static final String UNIT_EXPIRING_TITLE = "赛事 {match} 参赛单位 {unitId} 邀请码即将过期";
    public static final String UNIT_EXPIRING_CONTENT = "赛事 {match} 参赛单位 {unit} ID:{unitId} 邀请码即将过期";

    /**
     * 自动检查即将过期的 token，并发出提示通知
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkRefereeTokenExpiring() {
        log.info("定期检查 Referee Token 即将过期状况。");
        List<Match> expiring = mongoTemplate.find(Query.query(
                new Criteria().andOperator(
                        Criteria.where("active").is(true),
                        Criteria.where("refereeToken").ne(null),
                        Criteria.where("refereeToken.expirationTime").gt(Instant.now()),
                        Criteria.where("refereeToken.expirationTime").lt(Instant.now().plus(Duration.ofMinutes(30)))
                )
        ), Match.class);
        for (Match match : expiring) {
            notificationService.sendNotificationFromSystem(
                    match.getOrganizerUserId(),
                    REFEREE_EXPIRING_TITLE
                            .replace("{match}", match.getName()),
                    REFEREE_EXPIRING_CONTENT
                            .replace("{match}", match.getName())
                            .replace("{matchId}", match.getMatchId()),
                    NotificationTag.REFEREE_INVITE_EXPIRING,
                    ImmutableMap.of(
                            "matchId", match.getMatchId()
                    )
            );
        }
    }

    /**
     * 自动检查即将过期的 token，并发出提示通知
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkUnitTokenExpiring() {
        log.info("定期检查 Unit Token 即将过期状况。");
        List<Unit> expiring = mongoTemplate.find(Query.query(
                new Criteria().andOperator(
                        Criteria.where("unitToken").ne(null),
                        Criteria.where("unitToken.expirationTime").gt(Instant.now()),
                        Criteria.where("unitToken.expirationTime").lt(Instant.now().plus(Duration.ofMinutes(30)))
                )
        ), Unit.class);
        for (Unit unit : expiring) {
            List<String> matchNames = mongoTemplate.findDistinct(
                    Query.query(
                            new Criteria().andOperator(
                                    Criteria.where("active").is(true),
                                    Criteria.where("matchId").is(unit.getMatchId())
                            )
                    ), "name", Match.class, String.class
            );
            if (matchNames.size() != 1) {
                continue;
            }
            String matchName = matchNames.get(0);
            notificationService.sendNotificationFromSystem(
                    unit.getCreatorId(),
                    UNIT_EXPIRING_TITLE
                            .replace("{match}", matchName)
                            .replace("{unitId}", unit.getUnitId())
                    ,
                    UNIT_EXPIRING_CONTENT
                            .replace("{match}", matchName)
                            .replace("{unit}", unit.getName() == null ? "" : unit.getName())
                            .replace("{unitId}", unit.getUnitId()),
                    NotificationTag.UNIT_INVITE_EXPIRING,
                    ImmutableMap.of(
                            "matchId", unit.getMatchId(),
                            "unitId", unit.getUnitId()
                    )
            );
        }
    }
}
