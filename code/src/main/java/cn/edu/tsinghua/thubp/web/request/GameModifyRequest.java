package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.plugin.GameResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 修改比赛的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameModifyRequest implements ModifiableSource {
    @ApiModelProperty(value = "比赛的状态", required = false)
    @org.jetbrains.annotations.Nullable
    @AutoModify
    private GameStatus status;
    @ApiModelProperty(value = "参赛单位 0 的 ID", required = false)
    @org.jetbrains.annotations.Nullable
    private String unit0;
    @ApiModelProperty(value = "参赛单位 1 的 ID", required = false)
    @org.jetbrains.annotations.Nullable
    private String unit1;
    @ApiModelProperty(value = "比赛记分结果", required = false)
    @org.jetbrains.annotations.Nullable
    private GameResult result;
    @ApiModelProperty(value = "开始时间", required = false)
    @org.jetbrains.annotations.Nullable
    private Instant startTime;
    @ApiModelProperty(value = "地点", required = false)
    @org.jetbrains.annotations.Nullable
    private String location;
}
