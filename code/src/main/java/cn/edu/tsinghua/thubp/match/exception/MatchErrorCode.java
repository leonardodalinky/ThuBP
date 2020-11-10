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
    MATCH_TOKEN_EXPIRED_OR_INVALID(2003, HttpStatus.BAD_REQUEST, "赛事邀请码已过期或失效"),
    MATCH_REFEREE_TOKEN_EXPIRED_OR_INVALID(2004, HttpStatus.BAD_REQUEST, "裁判邀请码已过期或失效"),
    MATCH_ALREADY_REFEREE(2005, HttpStatus.BAD_REQUEST, "已经成为了裁判"),
    UNIT_NOT_FOUND(2006, HttpStatus.BAD_REQUEST, "未找到指定参赛单位"),
    UNIT_TOKEN_EXPIRED_OR_INVALID(2007, HttpStatus.BAD_REQUEST, "参赛单位邀请码已过期或失效"),
    UNIT_ALREADY_PARTICIPATED(2008, HttpStatus.BAD_REQUEST, "已经是此参赛单位的成员"),
    ROUND_UNIT_INVALID(2009, HttpStatus.BAD_REQUEST, "指定参赛单位有误"),
    ROUND_AUTO_EXCESSIVE(2010, HttpStatus.BAD_REQUEST, "自动生成的比赛数量过多"),
    ROUND_NOT_FOUND(2011, HttpStatus.BAD_REQUEST, "轮次未找到或不允许")
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
