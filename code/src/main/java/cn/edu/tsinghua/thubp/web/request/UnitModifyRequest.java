package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改参赛单位的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitModifyRequest {
    @ApiModelProperty(value = "赛事名字", required = false)
    private String name;
}
