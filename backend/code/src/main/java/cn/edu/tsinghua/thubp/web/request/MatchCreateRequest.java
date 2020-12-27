package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
import cn.edu.tsinghua.thubp.web.enums.IUploadType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.Instant;

/**
 * 创建赛事请求.
 * @author Rhacoal
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchCreateRequest implements ModifiableSource {
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
    @ApiModelProperty(value = "赛事预览图的文件名(key)", required = false)
    @Pattern(regexp = "^"+ IUploadType.STR_MATCH_PREVIEW + "_\\d+_[a-zA-Z0-9.-]+$")
    private String preview;
    @ApiModelProperty(value = "赛事预览大图的文件名(key)", required = false)
    @Pattern(regexp = "^"+ IUploadType.STR_MATCH_PREVIEW + "_\\d+_[a-zA-Z0-9.-]+$")
    private String previewLarge;
    @ApiModelProperty(value = "公开报名", required = true)
    @javax.validation.constraints.NotNull
    private Boolean publicSignUp;
    @ApiModelProperty(value = "公开查询", required = true)
    @javax.validation.constraints.NotNull
    private Boolean publicShowUp;
}
