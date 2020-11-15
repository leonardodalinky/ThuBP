package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.entity.UnitToken;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitInfoResponse extends SimpleResponse {
    @ApiModelProperty(value = "参赛单位名称", required = true)
    private String name;
    @ApiModelProperty(value = "参赛单位创建者 ID", required = true)
    private String creatorId;
    @ApiModelProperty(value = "相关联的赛事 ID", required = true)
    private String matchId;
    @ApiModelProperty(value = "参赛单位邀请码", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UnitToken unitToken;

    public UnitInfoResponse(Unit unit) {
        this.name = unit.getName();
        this.creatorId = unit.getCreatorId();
        this.matchId = unit.getMatchId();
        this.unitToken = unit.getUnitToken();
    }
}
