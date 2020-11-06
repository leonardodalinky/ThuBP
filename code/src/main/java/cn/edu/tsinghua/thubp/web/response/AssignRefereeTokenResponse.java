package cn.edu.tsinghua.thubp.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRefereeTokenResponse extends SimpleResponse {
    @ApiModelProperty(value = "赛事邀请码", required = true)
    private String refereeToken;
    @ApiModelProperty(value = "邀请码过期时间", required = true)
    private long expirationTime;
}
