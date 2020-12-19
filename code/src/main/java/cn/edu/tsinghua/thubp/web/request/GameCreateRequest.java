package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

/**
 * 创建比赛的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameCreateRequest {
    @ApiModelProperty(value = "参赛单位 0 的 ID", required = true)
    @NotBlank
    private String unit0;
    @ApiModelProperty(value = "参赛单位 1 的 ID", required = false)
    private String unit1;
    @ApiModelProperty(value = "开始时间", required = false)
    private Instant startTime;
    @ApiModelProperty(value = "地点", required = false)
    private String location;
    @ApiModelProperty(value = "裁判的用户 ID", required = false)
    private String referee;
}
