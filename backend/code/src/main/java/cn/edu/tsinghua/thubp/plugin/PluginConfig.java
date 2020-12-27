package cn.edu.tsinghua.thubp.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PluginConfig {
    @JsonProperty("plugin_id")
    @lombok.NonNull
    private final String pluginId;

    @JsonProperty("main_class")
    @lombok.NonNull
    private final String mainClass;

    @JsonProperty("name")
    @org.jetbrains.annotations.Nullable
    private final String name;

    @JsonProperty("description")
    @org.jetbrains.annotations.Nullable
    private final String description;

    @JsonProperty("version")
    @org.jetbrains.annotations.Nullable
    private final String version;
}
