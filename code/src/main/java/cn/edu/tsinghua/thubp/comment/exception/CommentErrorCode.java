package cn.edu.tsinghua.thubp.comment.exception;

import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommentErrorCode implements ErrorCode {
    MODERATION_FAILED(3001, HttpStatus.BAD_REQUEST, "文本审核失败"),
    COMMENT_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "评论未找到"),
    MATCH_REPLY_NOT_FOUND(3101, HttpStatus.NOT_FOUND, "未找到指定赛事的相关评论"),
    GAME_REPLY_NOT_FOUND(3102, HttpStatus.NOT_FOUND, "未找到指定比赛的相关评论"),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
