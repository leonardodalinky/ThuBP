package cn.edu.tsinghua.thubp.common.exception;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class CommonException extends RuntimeException {
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

}
