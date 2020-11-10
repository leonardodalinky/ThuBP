package cn.edu.tsinghua.thubp.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class TokenResponseBase {
    @ApiModelProperty(value = "邀请码", required = true)
    @NonNull
    private String token;
    @ApiModelProperty(value = "邀请码过期时间", required = true)
    @NonNull
    private long expirationTime;
}
