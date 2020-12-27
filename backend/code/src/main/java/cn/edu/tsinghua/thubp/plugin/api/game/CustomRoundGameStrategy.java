package cn.edu.tsinghua.thubp.plugin.api.game;

import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.misc.GameArrangement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;

/**
 * 自定义赛程安排策略.
 */
public interface CustomRoundGameStrategy {
    @Retention(RetentionPolicy.RUNTIME)
    @interface RoundGameStrategyInfo {
        String strategyId();
        String strategyName();
    }

    /**
     * 生成所需要的比赛集合.
     *
     * @param unitIds 参赛单位 ID 的列表.
     * @return 生成的比赛集合.
     */
    List<GameArrangement> generateGames(List<String> unitIds);
}
