package cn.edu.tsinghua.thubp.common.exception;

import com.google.common.collect.ImmutableMap;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonException extends RuntimeException implements GraphQLError {
    private final ErrorCode errorCode;
    private final transient HashMap<String, Object> data = new HashMap<>();

    /**
     * 构造一个异常.
     * @param errorCode 错误码
     * @param data 附加数据
     */
    public CommonException(ErrorCode errorCode, Map<String, Object> data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    CommonException(ErrorCode errorCode, Map<String, Object> data, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return ImmutableMap.of(
                "code", errorCode.getCode(),
                "status", errorCode.getStatus().value(),
                "message", errorCode.getMessage()
        );
    }
}
