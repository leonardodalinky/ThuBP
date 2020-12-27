package cn.edu.tsinghua.thubp.match.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoundStatus {
    NOT_START("NOT_START", "未开始"),
    RUNNING("RUNNING", "进行中"),
    FINISHED("FINISHED", "已完成"),
    TERMINATED("TERMINATED", "已中止");

    private final String name;
    private final String description;
}
