package cn.edu.tsinghua.thubp.user.exception;

import java.util.Map;

public class UserThuIdNotFoundException extends UserBaseException {
    public UserThuIdNotFoundException(Map<String, Object> data) {
        super(UserErrorCode.USER_NOT_FOUND, data);
    }
}
