package cn.edu.tsinghua.thubp.user.exception;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Link
 */
public abstract class UserBaseException extends RuntimeException {
    private final UserErrorCode userErrorCode;
    private final transient HashMap<String, Object> data = new HashMap<>();

    public UserBaseException(UserErrorCode userErrorCode, Map<String, Object> data) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    UserBaseException(UserErrorCode userErrorCode, Map<String, Object> data, Throwable cause) {
        super(userErrorCode.getMessage(), cause);
        this.userErrorCode = userErrorCode;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    public UserErrorCode getErrorCode() {
        return userErrorCode;
    }

    public Map<String, Object> getData() {
        return data;
    }

}
