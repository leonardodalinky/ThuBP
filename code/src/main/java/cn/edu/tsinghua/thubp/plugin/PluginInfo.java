package cn.edu.tsinghua.thubp.plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PluginInfo {
    private final String pluginId;
    private final String pluginName;
    private final Class<? extends PluginBase> pluginMainClass;
    private final String pluginMainClassName;
}
