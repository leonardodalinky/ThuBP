package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.plugin.api.config.GameConfig;
import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategy;
import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@PropertySource("classpath:config/plugin.properties")
public class PluginRegistryService {

    @Data
    @AllArgsConstructor
    public static class ScoreboardInfo {
        private final String scoreboardTypeId;
        private final String scoreboardTypeName;
        private final List<GameConfig.ConfigParameter> configParameters;
        private final Class<? extends GameResult> inputType;
    }

    public static final String MATCH_TYPE_ID = "matchTypeId";
    public static final String SCOREBOARD_TYPE_ID = "scoreboardTypeId";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, MatchType> matchTypeRegistry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CustomRoundGameStrategyType> roundGameStrategyRegistry = new ConcurrentHashMap<>();
    @Value("${plugin.naming.concat-plugin-id}")
    private boolean concatPluginId;

    public String getFullName(String pluginId, String keyId) {
        return concatPluginId ? pluginId + "#" + keyId : keyId;
    }

    /**
     * 注册赛事类型.
     * 赛事类型的完整 ID 是否带有插件 ID 前缀由 {@code /config/plugin.properties#plugin.naming.concat-plugin-id} 决定.
     *
     * @param pluginId      插件 ID
     * @param matchTypeId   赛事类型 ID
     * @param matchTypeName 赛事类型名称
     */
    public void registerMatchType(String pluginId, String matchTypeId, String matchTypeName) {
        String fullMatchTypeId = getFullName(pluginId, matchTypeId);
        matchTypeRegistry.put(fullMatchTypeId, new MatchType(fullMatchTypeId, matchTypeName));
    }

    @org.jetbrains.annotations.Nullable
    public MatchType getMatchType(String pluginId, String matchTypeId) {
        String fullMatchTypeId = getFullName(pluginId, matchTypeId);
        return matchTypeRegistry.get(fullMatchTypeId);
    }

    @org.jetbrains.annotations.Nullable
    public MatchType getMatchType(String fullMatchTypeId) {
        return matchTypeRegistry.get(fullMatchTypeId);
    }

    /**
     * 获取所有赛事类型.
     *
     * @return 赛事类型的集合
     */
    public Collection<MatchType> getAllMatchTypes() {
        return matchTypeRegistry.values();
    }

    public void registerRoundGameStrategy(CustomRoundGameStrategy customRoundGameStrategy) {
        CustomRoundGameStrategy.RoundGameStrategyInfo info = customRoundGameStrategy.getClass().getAnnotation(CustomRoundGameStrategy.RoundGameStrategyInfo.class);
        if (info == null) {
            throw new NullPointerException("RoundGameStrategy implementations must have a @RoundGameStrategyInfo annotation: " + customRoundGameStrategy.getClass().getName());
        }
        String strategyId = info.strategyId();
        if (this.roundGameStrategyRegistry.containsKey(strategyId)) {
            throw new IllegalStateException("Duplicated round game strategy: " + info.strategyId());
        }
        this.roundGameStrategyRegistry.put(strategyId, new CustomRoundGameStrategyType(strategyId, info.strategyName(), customRoundGameStrategy));
    }

    @org.jetbrains.annotations.Nullable
    public CustomRoundGameStrategyType getRoundGameStrategyType(String strategyId) {
        return this.roundGameStrategyRegistry.get(strategyId);
    }

    public Collection<CustomRoundGameStrategyType> getAllRoundGameStrategyTypes() {
        return this.roundGameStrategyRegistry.values();
    }

}
