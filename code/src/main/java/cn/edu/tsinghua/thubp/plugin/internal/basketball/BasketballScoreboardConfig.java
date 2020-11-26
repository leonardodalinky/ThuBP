package cn.edu.tsinghua.thubp.plugin.internal.basketball;

import cn.edu.tsinghua.thubp.plugin.api.config.GameConfig;
import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboardConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasketballScoreboardConfig implements GameScoreboardConfig {

    @GameConfig.FormParameter(key = "display_name", displayName = "名称")
    @GameConfig.Required
    @GameConfig.TextField(defaultValue = "默认规则")
    private String displayName;

    @GameConfig.FormParameter(key = "round_count", displayName = "比赛局数")
    @GameConfig.Required
    @GameConfig.IntegerField(defaultValue = 4)
    private int roundCount;

}
