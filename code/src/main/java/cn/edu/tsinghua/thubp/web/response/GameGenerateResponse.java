package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.match.misc.GameArrangement;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameGenerateResponse extends SimpleResponse {
    @ApiModelProperty(value = "生成的赛事安排", required = true)
    private List<GameArrangement> arrangement;
}
