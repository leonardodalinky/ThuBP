package cn.edu.tsinghua.thubp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示此 field 可通过名字，自动修改 entity
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoModify {
    /**
     * entity 中的属性名
     */
    String entityFieldName() default "";
    /**
     * request 中为 {@code null} 时，是否更新
     * 若为 {@code false}，则为 null 时不更新 entity 中内容
     */
    boolean nullable() default false;
}
