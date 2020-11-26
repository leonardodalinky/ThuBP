package cn.edu.tsinghua.thubp.plugin.internal.basketball;

import cn.edu.tsinghua.thubp.plugin.PluginBase;
import cn.edu.tsinghua.thubp.plugin.PluginConfig;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Basketball extends PluginBase {
    public static final PluginConfig BasketballPluginConfig = new PluginConfig(
            "internal.basketball",
            "cn.edu.tsinghua.thubp.plugin.internal.basketball.Basketball",
            "Basketball score support",
            "支持篮球记分",
            "0.1-alpha"
    );

    @Override
    public void onLoad() {
        getRegistry().registerMatchType("basketball", "篮球", new BasketballScoreboard());
    }
}
