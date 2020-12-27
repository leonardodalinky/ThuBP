package cn.edu.tsinghua.thubp.plugin.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomRoundGameStrategyType {
    private final String roundGameStrategyId;
    private final String roundGameStrategyName;
    private final CustomRoundGameStrategy customRoundGameStrategy;
}
