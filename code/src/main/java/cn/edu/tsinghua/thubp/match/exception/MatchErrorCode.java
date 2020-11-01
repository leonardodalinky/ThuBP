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
    MATCH_ALREADY_PARTICIPATED(2002, HttpStatus.BAD_REQUEST, "已经是此赛事的参与者"),
    MATCH_REFEREE_TOKEN_EXPIRED(2003, HttpStatus.BAD_REQUEST, "裁判邀请码已过期"),
    MATCH_REFEREE_TOKEN_NOT_ASSIGNED(2004, HttpStatus.BAD_REQUEST, "裁判邀请码未签发"),
    MATCH_ALREADY_REFEREE(2005, HttpStatus.BAD_REQUEST, "已经成为了裁判"),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
