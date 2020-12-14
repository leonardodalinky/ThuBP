package cn.edu.tsinghua.thubp.plugin.internal.example_strategy;

import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.match.misc.GameArrangement;
import cn.edu.tsinghua.thubp.plugin.PluginBase;
import cn.edu.tsinghua.thubp.plugin.PluginConfig;
import cn.edu.tsinghua.thubp.plugin.api.game.CustomRoundGameStrategy;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
public class OrderedKnockoutStrategy extends PluginBase {
    public static final PluginConfig ExampleStrategyConfig = new PluginConfig(
            "internal.example_strategy",
            "cn.edu.tsinghua.thubp.plugin.internal.example_strategy.OrderedKnockoutStrategy",
            "Example plugins for adding additional strategy",
            "自定义的赛程策略",
            "0.1-alpha"
    );

    @CustomRoundGameStrategy.RoundGameStrategyInfo(
            strategyId = "ORDERED_KNOCKOUT",
            strategyName = "淘汰赛(不打乱顺序)"
    )
    private static final class SpecialCustomRoundGameStrategy implements CustomRoundGameStrategy {
        @Override
        public List<GameArrangement> generateGames(List<String> unitIds) {
            List<GameArrangement> games = new ArrayList<>();
            for (int i = 0, m = unitIds.size() >> 1; i < m; ++i) {
                games.add(new GameArrangement(unitIds.get(i * 2), unitIds.get(i * 2 + 1)));
            }
            if ((unitIds.size() % 2) == 1) {
                games.add(new GameArrangement(unitIds.get(unitIds.size() - 1), null));
            }
            return games;
        }
    }

    @Override
    public void onLoad() {
        getRegistry().registerRoundGameStrategy(new SpecialCustomRoundGameStrategy());
    }
}
