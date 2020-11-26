package cn.edu.tsinghua.thubp.plugin.internal.basketball;

import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboard;
import lombok.AllArgsConstructor;
import lombok.Getter;

@GameScoreboard.GameScoreboardInfo(scoreboardTypeId = "default", scoreboardTypeName = "标准比赛")
public class BasketballScoreboard implements GameScoreboard<BasketballScoreboardConfig, BasketballGameResult> {
    @Override
    public BasketballScoreboardConfig buildDefaultConfig() {
        return null;
    }

    @Getter
    @AllArgsConstructor
    public enum BasketballValidationResult implements ValidationResult {
        OK(true, 0, "ok"),
        ;
        private final boolean valid;
        private final int code;
        private final String message;
    }

    @Override
    public ValidationResult isValid(BasketballScoreboardConfig config, BasketballGameResult basketballGameResult) {
        return BasketballValidationResult.OK;
    }
}
