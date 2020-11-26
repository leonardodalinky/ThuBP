package cn.edu.tsinghua.thubp.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteRefereesResponse extends SimpleResponse {
    @ApiModelProperty(value = "成功发送的用户 ID", required = true)
    private List<String> userIds;
}
