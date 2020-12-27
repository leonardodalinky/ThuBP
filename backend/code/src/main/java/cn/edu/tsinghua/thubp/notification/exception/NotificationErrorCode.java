package cn.edu.tsinghua.thubp.notification.exception;

import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_FAILED(5001, HttpStatus.INTERNAL_SERVER_ERROR, "通知未知错误"),
    NOTIFICATION_NOT_FOUND(5002, HttpStatus.BAD_REQUEST, "通知未找到"),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
