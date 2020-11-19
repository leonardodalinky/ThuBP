package cn.edu.tsinghua.thubp.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MatchType {
    private final String matchTypeId;
    private final String matchTypeName;
    private final List<PluginRegistryService.ScoreboardInfo> matchScoreboardTypes;

    public PluginRegistryService.ScoreboardInfo getScoreboardInfo(String scoreboardId) {
        if (scoreboardId == null) {
            return matchScoreboardTypes.get(0);
        }
        for (PluginRegistryService.ScoreboardInfo scoreboardInfo : matchScoreboardTypes) {
            if (scoreboardInfo.getScoreboardTypeId().equals(scoreboardId)) {
                return scoreboardInfo;
            }
        }
        return null;
    }

    @org.jetbrains.annotations.NotNull
    public PluginRegistryService.ScoreboardInfo getDefaultScoreboardInfo() {
        return matchScoreboardTypes.get(0);
    }
}
