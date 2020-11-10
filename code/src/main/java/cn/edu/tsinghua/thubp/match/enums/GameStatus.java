package cn.edu.tsinghua.thubp.match.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GameStatus {
    NOT_START("NOT_START", "未开始"),
    RUNNING("RUNNING", "进行中"),
    WIN_FIRST("WIN_FIRST", "参赛单位 0 胜利"),
    WIN_SECOND("WIN_SECOND", "参赛单位 1 胜利"),
    DRAW("DRAW", "平局"),
    ;

    private final String name;
    private final String description;
}
