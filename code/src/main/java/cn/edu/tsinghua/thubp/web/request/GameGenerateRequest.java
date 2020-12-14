package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 生成比赛的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameGenerateRequest {
    @ApiModelProperty(value = "参赛单位", required = true)
    private List<String> units;
    @ApiModelProperty(value = "生成策略", required = true)
    private String strategy;
}
