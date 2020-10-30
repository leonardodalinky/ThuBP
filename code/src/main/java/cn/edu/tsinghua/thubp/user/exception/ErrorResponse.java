package cn.edu.tsinghua.thubp.user.exception;

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
public class ErrorResponse {
    private int code;
    private int status;
    private String message;
    private String path;
    private Instant timestamp;
    private final HashMap<String, Object> errorDetail = new HashMap<>();

    public ErrorResponse(UserBaseException ex, String path) {
        this(ex.getErrorCode().getCode(), ex.getErrorCode().getStatus().value(), ex.getErrorCode().getMessage(), path, ex.getData());
    }

    public ErrorResponse(UserErrorCode userErrorCode, String path) {
        this(userErrorCode.getCode(), userErrorCode.getStatus().value(), userErrorCode.getMessage(), path, null);
    }

    public ErrorResponse(UserErrorCode userErrorCode, String path, Map<String, Object> errorDetail) {
        this(userErrorCode.getCode(), userErrorCode.getStatus().value(), userErrorCode.getMessage(), path, errorDetail);
    }

    private ErrorResponse(int code, int status, String message, String path, Map<String, Object> errorDetail) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
        if (!ObjectUtils.isEmpty(errorDetail)) {
            this.errorDetail.putAll(errorDetail);
        }
    }
}
