package cn.edu.tsinghua.thubp.common.entity;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.exception.ErrorCode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Token类型的基类。
 * @implNote TokenBase 不被记作单独的文档。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class TokenBase {

    @NonNull
    private String token;
    @NonNull
    private Instant expirationTime;

    public boolean valid(String token) {
        if (Instant.now().isAfter(expirationTime)) {
            return false;
        } else return token.equals(this.token);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expirationTime);
    }

    public abstract ErrorCode createException();

    public CommonException createException(String wrongToken) {
        return new CommonException(this.createException(), ImmutableMap.of("token", wrongToken));
    }
}
