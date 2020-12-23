package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDeleteRequest {
    @ApiModelProperty(value = "需要被删除的比赛 ID", required = true)
    @NotEmpty
    private List<String> games;
}
