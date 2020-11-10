package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRegisterRequest {
    // 这里不继承 TokenRequestBase 是因为此 field 可以忽略
    @ApiModelProperty(value = "报名赛事需要的邀请码", required = false)
    String token;
    @ApiModelProperty(value = "参赛单位名字", required = false)
    String unitName;
}
