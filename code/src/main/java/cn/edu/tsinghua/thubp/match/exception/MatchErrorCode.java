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
    MATCH_PUBLIC(2006, HttpStatus.BAD_REQUEST, "比赛为公开报名"),
    MATCH_UNIT_MIN_MAX_INVALID(2007, HttpStatus.BAD_REQUEST, "参赛单位最大最小人数无效"),
    MATCH_PARTICIPANT_NOT_FOUND(2008, HttpStatus.BAD_REQUEST, "赛事中未查询到指定参与者"),
    UNIT_NOT_FOUND(2101, HttpStatus.BAD_REQUEST, "未找到指定参赛单位"),
    UNIT_TOKEN_EXPIRED_OR_INVALID(2102, HttpStatus.BAD_REQUEST, "参赛单位邀请码已过期或失效"),
    UNIT_ALREADY_PARTICIPATED(2103, HttpStatus.BAD_REQUEST, "已经是此参赛单位的成员"),
    UNIT_MAX_MEMBER(2104, HttpStatus.BAD_REQUEST, "已达到小组最大参赛人数上限"),
    UNIT_PERMISSION_DENIED(2105, HttpStatus.BAD_REQUEST, "操作参赛单位权限不足"),
    UNIT_DELETE_SELF(2106, HttpStatus.BAD_REQUEST, "删除成员包含自身"),
    UNIT_DELETE_NOT_FOUND(2107, HttpStatus.BAD_REQUEST, "参赛单位中有未知人员"),
    UNIT_DELETE_MATCH_NOT_PREPARE(2108, HttpStatus.BAD_REQUEST, "赛事已开始"),
    ROUND_UNIT_INVALID(2201, HttpStatus.BAD_REQUEST, "指定参赛单位有误"),
    ROUND_AUTO_EXCESSIVE(2202, HttpStatus.BAD_REQUEST, "自动生成的比赛数量过多"),
    ROUND_NOT_FOUND(2203, HttpStatus.BAD_REQUEST, "轮次未找到或不允许"),
    ROUND_STRATEGY_UNKNOWN(2204, HttpStatus.BAD_REQUEST, "未知的自动生成策略"),
    GAME_NOT_FOUND(2301, HttpStatus.BAD_REQUEST, "未找到指定比赛"),
    GAME_UNIT_INVALID(2302, HttpStatus.BAD_REQUEST, "比赛的参赛方有误"),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
