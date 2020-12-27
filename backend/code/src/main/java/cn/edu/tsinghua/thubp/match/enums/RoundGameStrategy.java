package cn.edu.tsinghua.thubp.match.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Round 中自动生成 Game 的策略
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoundGameStrategy {
    SINGLE_ROUND("SINGLE_ROUND", "单循环赛制"),
    SINGLE_ROUND_HH("SINGLE_ROUND_HH", "单循环赛制(区分主客场)"),
    KNOCKOUT("KNOCKOUT", "淘汰制"),
    CUSTOM("CUSTOM", "自定义"),
    ;

    private final String name;
    private final String description;
}
