package cn.edu.tsinghua.thubp.user.exception;

import java.util.Map;

public class UserIdNotFoundException extends UserBaseException {
    public UserIdNotFoundException(Map<String, Object> data) {
        super(UserErrorCode.USER_NOT_FOUND, data);
    }
}
