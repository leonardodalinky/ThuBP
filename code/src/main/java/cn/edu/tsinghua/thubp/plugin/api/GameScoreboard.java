package cn.edu.tsinghua.thubp.plugin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 计分器类.
 * 当一场比赛结果确定后，应该通过这个类的实例进行记分结果的导出.
 * 对于注册的计分器类，这个类只会有一个实例. 由于不同的赛事对于同样的比赛可能会有不同的设置，所以这个类的实现应该是线程安全的.
 * 这个实例的大部分方法需要接受一个 Config 配置类，这意味着这是一个纯函数.
 * @param <Config> 配置类.
 */
public interface GameScoreboard<Config extends GameScoreboardConfig, Input> {

    /**
     * 输入是否合规的结果接口.
     * 这个方法会非常频繁的调用，我们推荐将这个类的实例缓存，或者使用 enum 实现这个接口，避免创建大量的小对象.
     */
    interface ValidationResult {
        /**
         * 输入是否符合要求.
         * @return 输入是否符合要求
         */
        boolean isValid();

        /**
         * 错误码. 这个方法的实现是可选的.
         * @return 错误码
         */
        default int getCode() {return 0;}

        /**
         * 关于不符合要求的文字说明. isValid 返回 false 的情况下，这个函数的返回应该非空.
         * @return 文字说明
         */
        String getMessage();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GameScoreboardInfo {
        String scoreboardTypeId();
        String scoreboardTypeName();
    }

    /**
     * 获得一个默认设置. 这个设置可能被更改.
     * @return 配置.
     */
    Config buildDefaultConfig();

    /**
     * 判定一个输入是否合规.
     * @param input 输入的字符串.
     * @return 输入是否合规.
     */
    ValidationResult isValid(Input input);

    default GameScoreboard.GameScoreboardInfo getInfo() {
        return this.getClass().getAnnotation(GameScoreboard.GameScoreboardInfo.class);
    }

}
