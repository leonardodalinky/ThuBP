package cn.edu.tsinghua.thubp.user.misc;

import cn.edu.tsinghua.thubp.user.enums.ThuIdentityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * 清华身份认证结果.
 */
@Data
@AllArgsConstructor
@Builder
public final class ThuAuthResult {
    @NonNull
    private final String thuId;
    @NonNull
    private final ThuIdentityType identityType;
    @NonNull
    private final String realName;
    @org.jetbrains.annotations.Nullable
    private final String email;
}
