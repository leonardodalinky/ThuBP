package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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
}
