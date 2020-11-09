package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.plugin.api.GameScoreboard;
import cn.edu.tsinghua.thubp.plugin.api.GameScoreboardConfig;
import cn.edu.tsinghua.thubp.plugin.api.config.GameConfig;
import com.google.common.collect.ImmutableList;
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
@PropertySource("/config/plugin.properties")
public class PluginRegistryService {

    @Data
    @AllArgsConstructor
    public static class ScoreboardInfo {
        private final String scoreboardTypeId;
        private final String scoreboardTypeName;
        private final List<GameConfig.ConfigParameter> configParameters;
        private final Class<? extends GameScoreboardConfig> configType;
        private final Class<?> inputType;
        private final GameScoreboard<?, ?> scoreboard;
    }

    private final ConcurrentHashMap<String, MatchType> matchTypeRegistry = new ConcurrentHashMap<>();
    @Value("${plugin.naming.concat-plugin-id}")
    private boolean concatPluginId;

    public String getFullName(String pluginId, String keyId) {
        return concatPluginId ? pluginId + "#" + keyId : keyId;
    }

    public <C extends GameScoreboardConfig, I> ScoreboardInfo extractScoreboardInfo(
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

    public void registerMatchType(String pluginId, String matchTypeId, String matchTypeName, GameScoreboard<?, ?>... scoreboards) {
        String fullMatchTypeId = getFullName(pluginId, matchTypeId);
        List<ScoreboardInfo> scoreboardInfos = Arrays.stream(scoreboards).map((scoreboard) -> {
            ScoreboardInfo info = extractScoreboardInfo(scoreboard);
            if (info == null) {
                throw new NullPointerException("Unregistered scoreboard type: " + info.getScoreboardTypeId());
            }
            return info;
        }).collect(Collectors.toList());
        matchTypeRegistry.put(fullMatchTypeId, new MatchType(fullMatchTypeId, matchTypeName, scoreboardInfos));
    }

    public MatchType getMatchType(String pluginId, String matchTypeId) {
        String fullMatchTypeId = getFullName(pluginId, matchTypeId);
        return matchTypeRegistry.get(fullMatchTypeId);
    }

    public Collection<MatchType> getAllMatchTypes() {
        return matchTypeRegistry.values();
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
            String fieldType;
            String[] selections = null;
            GameConfig.SelectionField selectionFieldAnnotation;
            GameConfig.TextField textFieldAnnotation;
            GameConfig.IntegerField integerFieldAnnotation;
            GameConfig.SwitchField switchFieldAnnotation;
            required = (field.getAnnotation(GameConfig.Required.class) != null);
            if ((textFieldAnnotation = field.getAnnotation(GameConfig.TextField.class)) != null) {
                fieldType = GameConfig.FIELD_TEXT;
                defaultValue = textFieldAnnotation.defaultValue();
            } else if ((integerFieldAnnotation = field.getAnnotation(GameConfig.IntegerField.class)) != null) {
                fieldType = GameConfig.FIELD_INTEGER;
                defaultValue = String.valueOf(integerFieldAnnotation.defaultValue());
            } else if ((selectionFieldAnnotation = field.getAnnotation(GameConfig.SelectionField.class)) != null) {
                fieldType = GameConfig.FIELD_SELECT;
                selections = selectionFieldAnnotation.value();
                defaultValue = selectionFieldAnnotation.defaultValue();
            } else if ((switchFieldAnnotation = field.getAnnotation(GameConfig.SwitchField.class)) != null) {
                fieldType = GameConfig.FIELD_SWITCH;
                defaultValue = switchFieldAnnotation.defaultValue() ? "true" : "false";
            } else {
                fieldType = GameConfig.FIELD_TEXT;
                defaultValue = "";
            }
            parameters.add(new GameConfig.ConfigParameter(key, displayName, defaultValue, required, fieldType, selections, keyOrder));
        }
        parameters.sort(Comparator.comparingInt(GameConfig.ConfigParameter::getKeyOrder));
        return ImmutableList.copyOf(parameters);
    }

}
