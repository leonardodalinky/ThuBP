package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
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
public class UnitModifyRequest implements ModifiableSource {
    @ApiModelProperty(value = "参赛单位名字", required = false)
    @AutoModify
    private String name;
}
