package cn.edu.tsinghua.thubp.user.exception;

import java.util.Map;

public class UserOldPwdNotProvidedException extends UserBaseException {
    public UserOldPwdNotProvidedException(Map<String, Object> data) {
        super(UserErrorCode.OLD_PASSWORD_NOT_PROVIDED, data);
    }
}
