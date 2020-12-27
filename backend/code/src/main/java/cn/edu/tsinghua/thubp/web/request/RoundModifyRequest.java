package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
import cn.edu.tsinghua.thubp.match.enums.RoundStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改轮次基本信息的请求.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoundModifyRequest implements ModifiableSource {
    @ApiModelProperty(value = "轮次的名字", required = false)
    @AutoModify
    private String name;
    @ApiModelProperty(value = "轮次的描述", required = false)
    @AutoModify
    private String description;
    @ApiModelProperty(value = "轮次的标签", required = false)
    @AutoModify
    private String tag;
    @ApiModelProperty(value = "轮次的状态", required = false)
    @AutoModify
    private RoundStatus status;
}
