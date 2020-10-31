package cn.edu.tsinghua.thubp.match.exception;

import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MatchErrorCode implements ErrorCode {
    MATCH_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "未找到指定赛事"),
    MATCH_ALREADY_PARTICIPATED(2002, HttpStatus.BAD_REQUEST, "已经是此赛事的参与者");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
