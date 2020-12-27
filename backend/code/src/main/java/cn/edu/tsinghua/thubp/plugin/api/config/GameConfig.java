package cn.edu.tsinghua.thubp.plugin.api.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public interface GameConfig extends Cloneable {

    enum FieldType {
        @JsonProperty("text")
        FIELD_TEXT,
        @JsonProperty("integer")
        FIELD_INTEGER,
        @JsonProperty("select")
        FIELD_SELECT,
        @JsonProperty("swich")
        FIELD_SWITCH
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Required {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FormParameter {
        String key() default "";
        String displayName() default "";
        int keyOrder() default 0;
    }

    /**
     * 单选. value 应该是 key, displayName, key, displayName, ... 形式.
     * key 不应该为空字符串.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SelectionField {
        String[] value();
        String defaultValue() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface TextField {
        String defaultValue() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IntegerField {
        int defaultValue() default 0;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SwitchField {
        boolean defaultValue() default false;
    }

    @Getter
    @AllArgsConstructor
    class ConfigParameter {
        private final String key;
        private final String displayName;
        private final String defaultValue;
        private final boolean required;
        private final FieldType fieldType;
        private String[] selections;
        private int keyOrder;
        private Field field;
    }
}
