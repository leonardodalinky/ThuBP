package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建赛事请求.
 * @author Rhacoal
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchCreateRequest {
    @ApiModelProperty(value = "赛事名字", required = true)
    @javax.validation.constraints.NotBlank
    String name;
    @ApiModelProperty(value = "赛事描述", required = true)
    @javax.validation.constraints.NotBlank
    String description;
    @ApiModelProperty(value = "面向人群", required = false)
    private String targetGroup;
    @javax.validation.constraints.NotBlank
    String matchTypeId;
    @ApiModelProperty(value = "公开报名", required = true)
    @javax.validation.constraints.NotNull
    Boolean publicSignUp;
}
