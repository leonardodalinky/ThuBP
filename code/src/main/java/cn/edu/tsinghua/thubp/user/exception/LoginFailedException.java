package cn.edu.tsinghua.thubp.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Link
 **/
public class LoginFailedException extends AuthenticationException {
    public LoginFailedException(String detail) {
        super(detail);
    }
}
