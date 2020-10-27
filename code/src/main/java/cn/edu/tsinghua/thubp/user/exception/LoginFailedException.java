package cn.edu.tsinghua.thubp.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Link
 * @createTime 2020年08月08日 20:57:00
 **/
public class LoginFailedException extends AuthenticationException {
    public LoginFailedException(String detail) {
        super(detail);
    }
}
