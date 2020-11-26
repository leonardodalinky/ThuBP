package cn.edu.tsinghua.thubp.plugin.internal.basketball;

import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboard;

@GameScoreboard.GameScoreboardInfo(scoreboardTypeId = "default", scoreboardTypeName = "标准比赛")
public class BasketballScoreboard implements GameScoreboard<BasketballScoreboardConfig, BasketballGameResult> {
    @Override
    public BasketballScoreboardConfig buildDefaultConfig() {
        return null;
    }

    @Override
    public ValidationResult isValid(BasketballScoreboardConfig config, BasketballGameResult basketballGameResult) {
        return null;
    }
}
