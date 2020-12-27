package cn.edu.tsinghua.thubp.common.util;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
import cn.edu.tsinghua.thubp.common.intf.ModifiableTarget;
import cn.edu.tsinghua.thubp.match.entity.Match;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 用于自动处理 modifyRequest 中的逻辑
 * @author AyajiLin
 */
public class AutoModifyUtil {
    public static void autoModify(ModifiableSource modifyRequest, ModifiableTarget entity) {
        Field[] requestFields = modifyRequest.getClass().getDeclaredFields();
        AutoModify autoModify = entity.getClass().getAnnotation(AutoModify.class);
        boolean isClass = entity.getClass().isAnnotationPresent(AutoModify.class);
        for (Field requestField : requestFields) {
            requestField.setAccessible(true);
            if (isClass || Objects.nonNull(autoModify = requestField.getDeclaredAnnotation(AutoModify.class))) {
                try {
                    Field matchField;
                    Object rf = requestField.get(modifyRequest);
                    if (autoModify.entityFieldName().length() == 0) {
                        // 如果未指定域名
                        matchField = entity.getClass().getDeclaredField(requestField.getName());
                    } else {
                        // 指定域名
                        matchField = entity.getClass().getDeclaredField(autoModify.entityFieldName());
                    }
                    matchField.setAccessible(true);
                    if (!autoModify.nullable() && Objects.nonNull(rf)) {
                        matchField.set(entity, rf);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // pass
                }
            }
        }
    }
}
