package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDeleteRequest {
    @ApiModelProperty(value = "需要被删除的比赛 ID", required = true)
    private List<String> games;
}
