package cn.edu.tsinghua.thubp.common.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class FieldCopier {
    /**
     * 尽量将 {@code src} 中的同名 field 的值，复制到 {@code dst} 中
     * @param src 源
     * @param dst 目标
     */
    public static void copy(@NotNull Object src, @NotNull Object dst) {
        Class<?> srcClazz = src.getClass();
        Field[] dstFields = dst.getClass().getDeclaredFields();
        for (Field dstField : dstFields) {
            dstField.setAccessible(true);
            Field srcField;
            try {
                srcField = srcClazz.getDeclaredField(dstField.getName());
                srcField.setAccessible(true);
                dstField.set(dst, srcField.get(src));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // pass
            }

        }
    }
}
