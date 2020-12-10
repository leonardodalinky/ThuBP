package cn.edu.tsinghua.thubp.notification.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationTag {
    NORMAL("NORMAL", "普通系统通知"),
    MATCH_INVITE("MATCH_INVITE", "赛事邀请"),
    REFEREE_INVITE("REFEREE_INVITE", "裁判邀请"),
    UNIT_INVITE("UNIT_INVITE", "参赛单位邀请"),
    MATCH_INVITE_EXPIRING("MATCH_INVITE_EXPIRING", "赛事邀请码即将过期"),
    REFEREE_INVITE_EXPIRING("REFEREE_INVITE_EXPIRING", "裁判邀请码即将过期"),
    UNIT_INVITE_EXPIRING("UNIT_INVITE_EXPIRING", "参赛单位邀请码即将过期"),
    ;

    private final String name;
    private final String description;
}
