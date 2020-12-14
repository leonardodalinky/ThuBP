package cn.edu.tsinghua.thubp.plugin.internal.game;

import cn.edu.tsinghua.thubp.plugin.PluginBase;
import cn.edu.tsinghua.thubp.plugin.PluginConfig;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InternalGameTypes extends PluginBase {
    public static final PluginConfig InternalGameTypesConfig = new PluginConfig(
            "internal.types",
            "cn.edu.tsinghua.thubp.plugin.internal.game.InternalGameTypes",
            "内置赛事类型",
            "内置赛事类型",
            "0.1-alpha"
    );

    @Override
    public void onLoad() {
        getRegistry().registerMatchType("basketball", "篮球");
        getRegistry().registerMatchType("tennis", "羽毛球");
    }
}
