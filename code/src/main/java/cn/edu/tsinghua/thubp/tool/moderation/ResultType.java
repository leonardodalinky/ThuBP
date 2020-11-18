package cn.edu.tsinghua.thubp.tool.moderation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResultType {
    BLOCK("BLOCK", "需要屏蔽"),
    REVIEW("REVIEW", "需要复查"),
    PASS("PASS", "通过")
    ;

    private final String name;
    private final String description;
}
