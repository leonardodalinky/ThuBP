package cn.edu.tsinghua.thubp.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonErrorCode implements ErrorCode {
    USER_NAME_ALREADY_EXIST(900, HttpStatus.BAD_REQUEST, "JSON 解析失败");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
