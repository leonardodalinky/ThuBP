package cn.edu.tsinghua.thubp.plugin.internal.basketball;

import cn.edu.tsinghua.thubp.plugin.GameResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class BasketballGameResult extends GameResult {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class RuleViolationRecord {
        /**
         * 违规的运动员用户 ID.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @org.jetbrains.annotations.Nullable
        private String userId;

        /**
         * 违规的发生时间点.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @org.jetbrains.annotations.Nullable
        private String time;

        /**
         * 违规的描述.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @org.jetbrains.annotations.Nullable
        private String description;
    }

    @JsonProperty("ruleViolationRecords")
    @org.jetbrains.annotations.Nullable
    private List<RuleViolationRecord> ruleViolationRecords;

}
