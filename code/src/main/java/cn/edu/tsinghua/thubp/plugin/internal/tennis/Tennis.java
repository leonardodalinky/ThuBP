package cn.edu.tsinghua.thubp.plugin.internal.tennis;

import cn.edu.tsinghua.thubp.plugin.PluginBase;
import cn.edu.tsinghua.thubp.plugin.PluginConfig;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Tennis extends PluginBase {
    public static final PluginConfig TennisPluginConfig = new PluginConfig(
            "internal.tennis",
            "cn.edu.tsinghua.thubp.plugin.internal.tennis.Tennis",
            "Tennis score support",
            "支持网球记分",
            "0.1-alpha"
    );

    @Override
    public void onLoad() {
        getRegistry().registerMatchType("tennis", "网球", new TennisScoreboard());
    }

}
