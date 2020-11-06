package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成为裁判请求.
 * @author Rhacoal
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BecomeRefereeRequest {
    @ApiModelProperty(value = "比赛邀请码", required = true)
    @javax.validation.constraints.NotBlank
    String refereeToken;
}
