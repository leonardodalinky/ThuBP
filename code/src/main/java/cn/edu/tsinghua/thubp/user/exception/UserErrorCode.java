package cn.edu.tsinghua.thubp.user.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode {
    USER_NAME_ALREADY_EXIST(1001, HttpStatus.BAD_REQUEST, "用户名已经存在"),
    USER_NOT_FOUND(1002, HttpStatus.NOT_FOUND, "未找到指定用户"),
    METHOD_ARGUMENT_NOT_VALID(1003, HttpStatus.BAD_REQUEST, "方法参数验证失败"),
    OLD_PASSWORD_NOT_PROVIDED(1004, HttpStatus.BAD_REQUEST, "旧密码缺失"),
    OLD_PASSWORD_NOT_VALID(1005, HttpStatus.BAD_REQUEST, "旧密码错误");


    private final int code;
    private final HttpStatus status;
    private final String message;
}
