package cn.edu.tsinghua.thubp.common.util;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Collection;

/**
 * 用于生成特殊的 Criteria.
 */
public class CriteriaUtil {
    /**
     * 如果 {@code values} 不为空，进行 $all 操作，否则不进行.
     * @param values $all 的 operand
     * @return 构造的 {@link Criteria}
     */
    public static Criteria whereContainsAll(String key, Collection<?> values) {
        if (!values.isEmpty()) {
            return Criteria.where(key).all(values);
        } else {
            return Criteria.where(key).exists(true);
        }
    }
}
