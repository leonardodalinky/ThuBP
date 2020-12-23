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
public class UserIdResponse extends SimpleResponse {
    @ApiModelProperty(value = "用户 ID", required = false)
    private String userId;
}
