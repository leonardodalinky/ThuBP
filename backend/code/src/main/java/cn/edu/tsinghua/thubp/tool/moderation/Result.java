package cn.edu.tsinghua.thubp.tool.moderation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huaweicloud.sdk.moderation.v1.model.RunTextModerationResponse;
import com.huaweicloud.sdk.moderation.v1.model.TextDetectionBody;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本审查的结果
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    /**
     * 检查结果
     */
    @lombok.NonNull
    private ResultType type;
    @lombok.NonNull
    private ResultInner detail;

    /**
     * 各种失败的结果
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultInner {
        /**
         * 色情
         */
        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Object porn;
        /**
         * 政治
         */
        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Object politics;
        /**
         * 广告
         */
        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Object ad;
        /**
         * 暴力
         */
        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Object abuse;
        /**
         * 违禁品
         */
        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Object contraband;
        /**
         * 灌水
         */
        @Nullable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Object flood;
    }

    public static Result fromRunTextModerationResponse(RunTextModerationResponse response) {
        TextDetectionBody body = response.getResult();
        String suggest = body.getSuggestion();
        Result result;
        if (suggest.equals("pass")) {
            result = new Result(ResultType.PASS, new ResultInner());
        } else if (suggest.equals("review")) {
            result = new Result(ResultType.REVIEW, new ResultInner());
        } else {
            result = new Result(ResultType.BLOCK, new ResultInner());
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) body.getDetail();
        // 给 inner 中赋值
        ResultInner inner = result.getDetail();
        Field[] fields = ResultInner.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.set(inner, map.get(field.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
