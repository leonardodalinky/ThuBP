package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDeleteMemberRequest {
    @ApiModelProperty(value = "需要被删除的成员 ID，不包括自身", required = true)
    @NotEmpty
    private List<String> members;
}
