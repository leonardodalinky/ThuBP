package cn.edu.tsinghua.thubp.plugin.exception;

import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PluginErrorCode implements ErrorCode {
    MATCH_TYPE_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "未找到指定比赛类型"),
    SCOREBOARD_TYPE_NOT_FOUND(4002, HttpStatus.NOT_FOUND, "未找到指定记分板类型"),
    SCOREBOARD_CONFIG_NOT_VALID(4003, HttpStatus.NOT_FOUND, "记分板配置有误"),
    SCOREBOARD_INPUT_NOT_VALID(4004, HttpStatus.NOT_FOUND, "比赛结果格式不正确"),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
