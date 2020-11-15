package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.web.enums.IUploadType;
import cn.edu.tsinghua.thubp.web.enums.UploadType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

/**
 * 修改赛事的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchModifyRequest {
    @ApiModelProperty(value = "赛事名字", required = false)
    private String name;
    @ApiModelProperty(value = "赛事描述", required = false)
    private String description;
    @ApiModelProperty(value = "赛事预览图的文件名(key)", required = false)
    @Pattern(regexp = "^"+ IUploadType.STR_MATCH_PREVIEW + "-[a-zA-Z0-9.-]+$")
    private String preview;
    @ApiModelProperty(value = "赛事预览大图的文件名(key)", required = false)
    @Pattern(regexp = "^"+ IUploadType.STR_MATCH_PREVIEW + "-[a-zA-Z0-9.-]+$")
    private String previewLarge;
    @ApiModelProperty(value = "是否开放报名创建参赛单位", required = false)
    private Boolean publicSignUp;
}
