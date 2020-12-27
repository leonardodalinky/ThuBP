package cn.edu.tsinghua.thubp.common.util;

/**
 * 一些关于时间的公用函数.
 * @author Rhacoal
 */
public class TimeUtil {
    private static final long DAY_SECONDS = 86400 * 1000;

    /**
     * 获取 dayCount 天后的时间戳 (毫秒)
     * @param dayCount 天数。
     * @return dayCount 天后的时间戳 (毫秒)
     */
    public static long getFutureTimeMillisByDays(int dayCount) {
        return System.currentTimeMillis() + dayCount * DAY_SECONDS;
    }
}
