package cn.edu.tsinghua.thubp.plugin.internal.tennis;

import cn.edu.tsinghua.thubp.plugin.GameResult;
import cn.edu.tsinghua.thubp.plugin.api.GameScoreboard;
import lombok.AllArgsConstructor;
import lombok.Getter;

@GameScoreboard.GameScoreboardInfo(scoreboardTypeId = "default", scoreboardTypeName = "标准比赛")
public class TennisScoreboard implements GameScoreboard<TennisScoreboardConfig, GameResult> {

    @Getter
    @AllArgsConstructor
    public enum TennisValidationResult implements ValidationResult {
        OK(true, 0, "ok"),
        INVALID_ROUND_SCORE(false, 1, "局分数不合理"),
        ;
        private final boolean valid;
        private final int code;
        private final String message;
    }

    @Override
    public TennisScoreboardConfig buildDefaultConfig() {
        return new TennisScoreboardConfig("默认赛事", 5);
    }

    @Override
    public ValidationResult isValid(GameResult gameResult) {
        return TennisValidationResult.OK;
    }

}
