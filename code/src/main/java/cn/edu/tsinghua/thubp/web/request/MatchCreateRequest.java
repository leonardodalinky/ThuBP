package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
    private String name;
    @ApiModelProperty(value = "赛事描述", required = true)
    @javax.validation.constraints.NotBlank
    private String description;
    @ApiModelProperty(value = "面向人群", required = false)
    private String targetGroup;
    @ApiModelProperty(value = "开始时间", required = false)
    private Instant startTime;
    @javax.validation.constraints.NotBlank
    private String matchTypeId;
    @ApiModelProperty(value = "参赛单位有效最小人数", required = true)
    @javax.validation.constraints.NotNull
    @javax.validation.constraints.Min(value = 1)
    private Integer minUnitMember;
    @ApiModelProperty(value = "参赛单位最大人数", required = true)
    @javax.validation.constraints.Min(value = 1)
    private Integer maxUnitMember;
    @ApiModelProperty(value = "公开报名", required = true)
    @javax.validation.constraints.NotNull
    private Boolean publicSignUp;
    @ApiModelProperty(value = "公开查询", required = true)
    @javax.validation.constraints.NotNull
    private Boolean publicShowUp;
}
