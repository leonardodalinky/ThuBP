package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.enums.RoundGameStrategy;
import cn.edu.tsinghua.thubp.plugin.MatchType;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategyType;
import cn.edu.tsinghua.thubp.web.graphql.misc.RoundGameStrategyType;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PluginQueryResolver implements GraphQLQueryResolver {
    private final PluginRegistryService pluginRegistryService;

    public List<MatchType> listMatchTypes() {
        return ImmutableList.copyOf(pluginRegistryService.getAllMatchTypes());
    }

    public List<RoundGameStrategyType> listRoundGameStrategyTypes() {
        ArrayList<RoundGameStrategyType> list = new ArrayList<>();
        for (RoundGameStrategy roundGameStrategy : RoundGameStrategy.values()) {
            list.add(new RoundGameStrategyType(roundGameStrategy.getName(), roundGameStrategy.getDescription()));
        }
        for (CustomRoundGameStrategyType customRoundGameStrategyType : pluginRegistryService.getAllRoundGameStrategyTypes()) {
            list.add(new RoundGameStrategyType(customRoundGameStrategyType.getRoundGameStrategyId(),
                    customRoundGameStrategyType.getRoundGameStrategyName()));
        }
        return list;
    }
}
