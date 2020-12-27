package cn.edu.tsinghua.thubp.common.util;

/**
 * 一些公用的数学方法.
 * @author Rhacoal
 */
public class MathUtil {
    /**
     * 将 int 裁剪到 min 和 max 之间.
     * @param value 待裁剪的数值
     * @param min 最小值
     * @param max 最大值
     * @return 裁剪后的数值.
     */
    public static int limitInRange(int value, int min, int max) {
        return min <= max ? Math.min(max, Math.max(min, value)) : Math.min(min, Math.max(max, value));
    }

    /**
     * 将 long 裁剪到 min 和 max 之间.
     * @param value 待裁剪的数值
     * @param min 最小值
     * @param max 最大值
     * @return 裁剪后的数值.
     */
    public static long limitInRange(long value, long min, long max) {
        return min <= max ? Math.min(max, Math.max(min, value)) : Math.min(min, Math.max(max, value));
    }
}
