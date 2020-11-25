package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.common.util.FieldCopier;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.MatchToken;
import cn.edu.tsinghua.thubp.match.entity.RefereeToken;
import cn.edu.tsinghua.thubp.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URL;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MatchInfoResponse extends SimpleResponse {
    @ApiModelProperty(value = "赛事 ID", required = true)
    private String matchId;
    @ApiModelProperty(value = "组织者 ID", required = true)
    private String organizerUserId;
    @ApiModelProperty(value = "赛事名字", required = true)
    private String name;
    @ApiModelProperty(value = "赛事描述", required = true)
    private String description;
    @ApiModelProperty(value = "面向人群", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String targetGroup;
    @ApiModelProperty(value = "赛事预览图", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private URL preview;
    @ApiModelProperty(value = "赛事预览大图", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private URL previewLarge;
    @ApiModelProperty(value = "赛事类型 ID", required = true)
    private String matchTypeId;
    @ApiModelProperty(value = "裁判邀请码", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RefereeToken refereeToken;
    @ApiModelProperty(value = "公开报名", required = true)
    private Boolean publicSignUp;
    @ApiModelProperty(value = "赛事邀请码", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MatchToken matchToken;
    @ApiModelProperty(value = "创建时间", required = true)
    private Instant createdAt;

    public MatchInfoResponse(Match match) {
        FieldCopier.copy(match, this);
    }
}
