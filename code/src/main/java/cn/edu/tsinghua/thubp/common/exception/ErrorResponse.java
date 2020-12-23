package cn.edu.tsinghua.thubp.common.exception;

import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
public class ErrorResponse extends SimpleResponse {
    private int code;
    private int status;
    private String path;
    private Instant timestamp;
    private final HashMap<String, Object> errorDetail = new HashMap<>();

    public ErrorResponse(CommonException ex, String path) {
        this(ex.getErrorCode().getCode(), ex.getErrorCode().getStatus().value(), ex.getErrorCode().getMessage(), path, ex.getData());
    }

    public ErrorResponse(ErrorCode errorCode, String path) {
        this(errorCode.getCode(), errorCode.getStatus().value(), errorCode.getMessage(), path, null);
    }

    public ErrorResponse(ErrorCode errorCode, String path, Map<String, Object> errorDetail) {
        this(errorCode.getCode(), errorCode.getStatus().value(), errorCode.getMessage(), path, errorDetail);
    }

    private ErrorResponse(int code, int status, String message, String path, Map<String, Object> errorDetail) {
        super(message);
        this.code = code;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now();
        if (!ObjectUtils.isEmpty(errorDetail)) {
            this.errorDetail.putAll(errorDetail);
        }
    }
}
