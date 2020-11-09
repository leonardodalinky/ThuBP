package cn.edu.tsinghua.thubp.plugin.internal.tennis;

import cn.edu.tsinghua.thubp.plugin.api.GameScoreboardConfig;
import cn.edu.tsinghua.thubp.plugin.api.config.GameConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TennisScoreboardConfig implements GameScoreboardConfig {

    @GameConfig.FormParameter(key = "display_name", displayName = "名称")
    @GameConfig.Required
    @GameConfig.TextField(defaultValue = "默认规则")
    private String displayName;

    @GameConfig.FormParameter(key = "round_count", displayName = "比赛局数")
    @GameConfig.Required
    @GameConfig.IntegerField(defaultValue = 6)
    private int roundCount;

}
