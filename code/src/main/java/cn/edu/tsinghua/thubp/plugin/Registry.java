package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategy;
import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboard;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Registry {

    private final PluginRegistryService pluginRegistryService;
    /**
     * 插件的 ID.
     * 这个 ID 会用于完整的字符串名前缀，所以不要轻易更改 pluginId.
     */
    private final String pluginId;

    /**
     * 注册一个赛事类型.
     * 赛事类型中的 matchTypeId
     * @param matchTypeId 赛事类型 ID. 这个 ID 是内部 ID.
     * @param matchTypeName 赛事类型名称.
     * @param scoreboards 记分器类型.
     */
    public void registerMatchType(String matchTypeId, String matchTypeName, GameScoreboard...scoreboards) {
        pluginRegistryService.registerMatchType(this.pluginId, matchTypeId, matchTypeName, scoreboards);
    }

    /**
     * 注册一个赛程策略.
     * @param customRoundGameStrategy 赛程策略对象. 该对象的类应该有一个 {@link CustomRoundGameStrategy.RoundGameStrategyInfo} annotation.
     */
    public void registerRoundGameStrategy(CustomRoundGameStrategy customRoundGameStrategy) {
        pluginRegistryService.registerRoundGameStrategy(customRoundGameStrategy);
    }

}
