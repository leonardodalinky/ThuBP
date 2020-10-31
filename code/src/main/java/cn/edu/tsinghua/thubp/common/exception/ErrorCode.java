package cn.edu.tsinghua.thubp.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 错误码 enum 应该实现这个接口.
 * @see cn.edu.tsinghua.thubp.user.exception.UserErrorCode
 * @see cn.edu.tsinghua.thubp.match.exception.MatchErrorCode
 */
public interface ErrorCode {
    int getCode();
    HttpStatus getStatus();
    String getMessage();
}
