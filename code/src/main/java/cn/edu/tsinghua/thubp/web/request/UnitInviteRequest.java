package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitInviteRequest {
    @ApiModelProperty(value = "邀请的人员 ID", required = true)
    @javax.validation.constraints.NotBlank
    private String[] userIds;
}
