package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRegisterRequest {
    // 这里不继承 TokenRequestBase 是因为此 field 可以忽略
    @ApiModelProperty(value = "报名赛事需要的邀请码", required = false)
    String token;
    @ApiModelProperty(value = "参赛单位名字", required = true)
    @NotNull
    String unitName;
    @ApiModelProperty(value = "个人描述")
    @javax.validation.constraints.Size(max = 1000)
    private String description;
}
