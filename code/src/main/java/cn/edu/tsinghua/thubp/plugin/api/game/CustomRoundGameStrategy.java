package cn.edu.tsinghua.thubp.plugin.api.game;

import cn.edu.tsinghua.thubp.match.entity.Game;

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
     * 返回的比赛集合中，每个成员 Game 只需要指定 unit0 和 unit1，其它成员会由调用方填充.
     * 在添加比赛到一个轮次时，会使用顺序遍历添加，故 Collection 最好是有序的集合.
     *
     * @param unitIds 参赛单位 ID 的列表.
     * @return 生成的比赛集合.
     */
    Collection<Game> generateGames(List<String> unitIds);
}
