package cn.edu.tsinghua.thubp.match.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MatchStatus {
    PREPARE("PREPARE", "未开始"),
    RUNNING("RUNNING", "进行中"),
    FINISHED("FINISHED", "已结束"),
    ;

    private final String name;
    private final String description;
}
