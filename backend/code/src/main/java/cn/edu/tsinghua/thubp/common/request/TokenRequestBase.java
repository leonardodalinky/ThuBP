package cn.edu.tsinghua.thubp.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 必须拥有邀请码的请求的基类
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequestBase {
    @ApiModelProperty(value = "邀请码", required = true)
    @javax.validation.constraints.NotBlank
    String token;
}
