package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改比赛的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameModifyRequest {
    @ApiModelProperty(value = "比赛的状态", required = false)
    @org.jetbrains.annotations.Nullable
    private GameStatus status;
    @ApiModelProperty(value = "参赛单位 0 的 ID", required = false)
    @org.jetbrains.annotations.Nullable
    private String unit0;
    @ApiModelProperty(value = "参赛单位 1 的 ID", required = false)
    @org.jetbrains.annotations.Nullable
    private String unit1;
}
