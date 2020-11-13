package cn.edu.tsinghua.thubp.common.response;

import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class TokenResponseBase extends SimpleResponse {
    @ApiModelProperty(value = "邀请码", required = true)
    @NonNull
    private String token;
    @ApiModelProperty(value = "邀请码过期时间", required = true)
    @NonNull
    private long expirationTime;
}
