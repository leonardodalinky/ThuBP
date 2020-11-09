package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.plugin.api.GameScoreboard;
import cn.edu.tsinghua.thubp.plugin.api.GameScoreboardConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Registry {

    private PluginRegistryService pluginRegistryService;
    /**
     * 插件的 ID.
     * 这个 ID 会用于完整的字符串名前缀，所以不要轻易更改 pluginId.
     */
    private String pluginId;

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

}
