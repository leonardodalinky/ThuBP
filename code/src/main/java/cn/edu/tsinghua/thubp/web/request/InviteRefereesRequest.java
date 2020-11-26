package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteRefereesRequest {
    @ApiModelProperty(value = "邀请的裁判用户 ID", required = true)
    private List<String> userIds;
}
