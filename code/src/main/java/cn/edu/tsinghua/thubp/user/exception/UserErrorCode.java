package cn.edu.tsinghua.thubp.user.exception;

import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {
    USER_NAME_ALREADY_EXIST(1001, HttpStatus.BAD_REQUEST, "用户名已经存在"),
    USER_NOT_FOUND(1002, HttpStatus.NOT_FOUND, "未找到指定用户"),
    METHOD_ARGUMENT_NOT_VALID(1003, HttpStatus.BAD_REQUEST, "方法参数验证失败"),
    OLD_PASSWORD_NOT_PROVIDED(1004, HttpStatus.BAD_REQUEST, "旧密码缺失"),
    OLD_PASSWORD_NOT_VALID(1005, HttpStatus.BAD_REQUEST, "旧密码错误"),
    THUID_ALREADY_EXIST(1006, HttpStatus.BAD_REQUEST, "证件号已经存在"),
    THUAUTH_RESPONSE_NOT_VALID(1101, HttpStatus.BAD_REQUEST, "清华统一身份验证失败"),
    THUAUTH_USER_IDENTITY_NOT_VALID(1102, HttpStatus.BAD_REQUEST, "无法识别的清华身份类别"),
    ;


    private final int code;
    private final HttpStatus status;
    private final String message;
}
