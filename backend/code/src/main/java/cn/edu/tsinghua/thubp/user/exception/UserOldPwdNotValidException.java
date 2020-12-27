package cn.edu.tsinghua.thubp.user.exception;

import java.util.Map;

public class UserOldPwdNotValidException extends UserBaseException {
    public UserOldPwdNotValidException(Map<String, Object> data) {
        super(UserErrorCode.OLD_PASSWORD_NOT_VALID, data);
    }
}
