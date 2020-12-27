package cn.edu.tsinghua.thubp.user.exception;

import cn.edu.tsinghua.thubp.common.exception.CommonException;

import java.util.Map;

/**
 * @author Link
 */
public abstract class UserBaseException extends CommonException {

    public UserBaseException(UserErrorCode userErrorCode, Map<String, Object> data) {
        super(userErrorCode, data);
    }

}
