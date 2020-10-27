package cn.edu.tsinghua.thubp.user.exception;

import java.util.Map;

/**
 * @author Link
 */
public class UsernameNotFoundException extends BaseException {
    public UsernameNotFoundException(Map<String, Object> data) {
        super(UserErrorCode.USER_NAME_NOT_FOUND, data);
    }
}
