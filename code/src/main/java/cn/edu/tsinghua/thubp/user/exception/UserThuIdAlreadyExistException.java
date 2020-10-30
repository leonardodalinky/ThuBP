package cn.edu.tsinghua.thubp.user.exception;

import java.util.Map;

/**
 * @author Link
 */
public class UserThuIdAlreadyExistException extends UserBaseException {

    public UserThuIdAlreadyExistException(Map<String, Object> data) {
        super(UserErrorCode.USER_NAME_ALREADY_EXIST, data);
    }
}
