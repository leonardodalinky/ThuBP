package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategy;
import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategyType;
import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboard;
import cn.edu.tsinghua.thubp.plugin.api.scoreboard.GameScoreboardConfig;
import cn.edu.tsinghua.thubp.plugin.api.config.GameConfig;
import cn.edu.tsinghua.thubp.plugin.exception.PluginErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:config/plugin.properties")
public class PluginRegistryService {

    @Data
    @AllArgsConstructor
    public static class ScoreboardInfo {
        private final String scoreboardTypeId;
        private final String scoreboardTypeName;
        private final List<GameConfig.ConfigParameter> configParameters;
        private final Class<? extends GameScoreboardConfig> configType;
        private final Class<? extends GameResult> inputType;
        private final GameScoreboard<?, ?> scoreboard;

        public Object buildDefaultConfig() {
            return scoreboard.buildDefaultConfig();
        }

        public Object convertTreeToConfig(JsonNode jsonNode) {
            try {
                Object objectConfig = objectMapper.treeToValue(jsonNode, configType);
                for (GameConfig.ConfigParameter parameter : configParameters) {
                    switch (parameter.getFieldType()) {
                        case FIELD_INTEGER:
                            if (!jsonNode.has(parameter.getKey()) && !parameter.getDefaultValue().isEmpty()) {
                                parameter.getField().setInt(objectConfig, Integer.parseInt(parameter.getDefaultValue()));
                            }
                            break;
                        case FIELD_SELECT:
                            if (!jsonNode.has(parameter.getKey()) && !parameter.getDefaultValue().isEmpty()) {
                                parameter.getField().set(objectConfig, parameter.getDefaultValue());
                            }
                            boolean ok = false;
                            String value = (String) parameter.getField().get(objectConfig);
                            for (String selection : parameter.getSelections()) {
                                if (Objects.equals(selection, value)) {
                                    ok = true;
                                    break;
                                }
                            }
                            if (!ok) {
                                return null;
                            }
                            break;
                        case FIELD_SWITCH:
                            if (!jsonNode.has(parameter.getKey()) && !parameter.getDefaultValue().isEmpty()) {
                                parameter.getField().setBoolean(objectConfig, Boolean.parseBoolean(parameter.getDefaultValue()));
                            }
                            break;
                        case FIELD_TEXT:
                            if (!jsonNode.has(parameter.getKey()) && !parameter.getDefaultValue().isEmpty()) {
                                parameter.getField().set(objectConfig, parameter.getDefaultValue());
                            }
                            break;
                        default:
                            break;
                    }
                }
                return objectConfig;
            } catch (JsonProcessingException | IllegalAccessException e) {
                return null;
            }
        }

        public Object convertTreeToInput(JsonNode input) {
            try {
                return objectMapper.treeToValue(input, inputType);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        public GameScoreboard.ValidationResult verifyInput(GameScoreboardConfig config, GameResult input) {
            return scoreboard.isInputObjectValid(config, input);
        }
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

    public <C extends GameScoreboardConfig, I extends GameResult> ScoreboardInfo extractScoreboardInfo(
            GameScoreboard<C, I> gameScoreboard) {
        GameScoreboard.GameScoreboardInfo info = gameScoreboard.getInfo();
        assert info != null;
        Type[] interfaces = gameScoreboard.getClass().getGenericInterfaces();
        Class<?>[] interfaces1 = gameScoreboard.getClass().getInterfaces();
        Type type = null;
        for (int i = 0; i < interfaces.length; ++i) {
            if (GameScoreboard.class.isAssignableFrom(interfaces1[i])) {
                type = interfaces[i];
            }
        }
        assert type != null;
        Type[] generics = ((ParameterizedType) type).getActualTypeArguments();
        Class<C> configType = (Class<C>) generics[0];
        Class<I> inputType = (Class<I>) generics[1];
        List<GameConfig.ConfigParameter> configParameters = dumpConfigParameters(configType);
        return new ScoreboardInfo(info.scoreboardTypeId(), info.scoreboardTypeName(), configParameters, configType, inputType, gameScoreboard);
    }

    /**
     * 注册赛事类型.
     * 赛事类型的完整 ID 是否带有插件 ID 前缀由 {@code /config/plugin.properties#plugin.naming.concat-plugin-id} 决定.
     *
     * @param pluginId      插件 ID
     * @param matchTypeId   赛事类型 ID
     * @param matchTypeName 赛事类型名称
     * @param scoreboards   计分器类型. 第一个是默认的，但至少要有一个.
     */
    public void registerMatchType(String pluginId, String matchTypeId, String matchTypeName, GameScoreboard<?, ?>... scoreboards) {
        if (scoreboards.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Missing scoreboards");
        }
        String fullMatchTypeId = getFullName(pluginId, matchTypeId);
        List<ScoreboardInfo> scoreboardInfos = Arrays.stream(scoreboards).map((scoreboard) -> {
            ScoreboardInfo info = extractScoreboardInfo(scoreboard);
            if (info == null) {
                throw new NullPointerException("Missing annotation @GameScoreboardInfo for scoreboard: "
                        + scoreboard.getClass().getName());
            }
            return info;
        }).collect(Collectors.toList());
        matchTypeRegistry.put(fullMatchTypeId, new MatchType(fullMatchTypeId, matchTypeName, scoreboardInfos));
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

    @org.jetbrains.annotations.NotNull
    public ScoreboardInfo getScoreboardInfo(String fullMatchTypeId, String scoreboardTypeId) {
        MatchType matchType = this.getMatchType(fullMatchTypeId);
        if (matchType == null) {
            throw new CommonException(PluginErrorCode.MATCH_TYPE_NOT_FOUND, ImmutableMap.of(MATCH_TYPE_ID, fullMatchTypeId));
        }
        PluginRegistryService.ScoreboardInfo scoreboardInfo = matchType.getScoreboardInfo(scoreboardTypeId);
        if (scoreboardInfo == null) {
            throw new CommonException(PluginErrorCode.MATCH_TYPE_NOT_FOUND, ImmutableMap.of(SCOREBOARD_TYPE_ID, scoreboardTypeId));
        }
        return scoreboardInfo;
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

    private static List<GameConfig.ConfigParameter> dumpConfigParameters(Class<? extends GameScoreboardConfig> configClass) {
        List<GameConfig.ConfigParameter> parameters = new ArrayList<>();
        Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            GameConfig.FormParameter formParameter = field.getAnnotation(GameConfig.FormParameter.class);
            if (formParameter == null) {
                // ignore this parameter
                continue;
            }
            int keyOrder = formParameter.keyOrder();
            String key = formParameter.key();
            String displayName = formParameter.displayName();
            String defaultValue;
            boolean required;
            GameConfig.FieldType fieldType;
            String[] selections = null;
            GameConfig.SelectionField selectionFieldAnnotation;
            GameConfig.TextField textFieldAnnotation;
            GameConfig.IntegerField integerFieldAnnotation;
            GameConfig.SwitchField switchFieldAnnotation;
            required = (field.getAnnotation(GameConfig.Required.class) != null);
            if ((textFieldAnnotation = field.getAnnotation(GameConfig.TextField.class)) != null) {
                fieldType = GameConfig.FieldType.FIELD_TEXT;
                defaultValue = textFieldAnnotation.defaultValue();
            } else if ((integerFieldAnnotation = field.getAnnotation(GameConfig.IntegerField.class)) != null) {
                fieldType = GameConfig.FieldType.FIELD_INTEGER;
                defaultValue = String.valueOf(integerFieldAnnotation.defaultValue());
            } else if ((selectionFieldAnnotation = field.getAnnotation(GameConfig.SelectionField.class)) != null) {
                fieldType = GameConfig.FieldType.FIELD_SELECT;
                selections = selectionFieldAnnotation.value();
                defaultValue = selectionFieldAnnotation.defaultValue();
            } else if ((switchFieldAnnotation = field.getAnnotation(GameConfig.SwitchField.class)) != null) {
                fieldType = GameConfig.FieldType.FIELD_SWITCH;
                defaultValue = switchFieldAnnotation.defaultValue() ? "true" : "false";
            } else {
                fieldType = GameConfig.FieldType.FIELD_TEXT;
                defaultValue = "";
            }
            parameters.add(new GameConfig.ConfigParameter(key, displayName, defaultValue, required, fieldType, selections, keyOrder, field));
        }
        parameters.sort(Comparator.comparingInt(GameConfig.ConfigParameter::getKeyOrder));
        return ImmutableList.copyOf(parameters);
    }

}
