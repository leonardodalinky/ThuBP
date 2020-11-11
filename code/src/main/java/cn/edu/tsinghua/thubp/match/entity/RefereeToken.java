package cn.edu.tsinghua.thubp.match.entity;

import cn.edu.tsinghua.thubp.common.entity.TokenBase;
import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 裁判报名时的邀请码
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class RefereeToken extends TokenBase {
    @Override
    public ErrorCode getErrorCode() {
        return MatchErrorCode.MATCH_REFEREE_TOKEN_EXPIRED_OR_INVALID;
    }
}
