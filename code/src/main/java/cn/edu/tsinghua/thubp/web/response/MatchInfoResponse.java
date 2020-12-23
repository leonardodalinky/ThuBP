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
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchInfoResponse extends SimpleResponse {
    @ApiModelProperty(value = "赛事 ID", required = true)
    private String matchId;
    @ApiModelProperty(value = "赛事是否结束", required = true)
    private Boolean active;
    @ApiModelProperty(value = "组织者 ID", required = true)
    private String organizerUserId;
    @ApiModelProperty(value = "赛事名字", required = true)
    private String name;
    @ApiModelProperty(value = "赛事描述", required = true)
    private String description;
    @ApiModelProperty(value = "面向人群", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String targetGroup;
    @ApiModelProperty(value = "开始时间", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant startTime;
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
    @ApiModelProperty(value = "参赛单位有效最小人数", required = true)
    private Integer minUnitMember;
    @ApiModelProperty(value = "参赛单位最大人数", required = true)
    private Integer maxUnitMember;
    @ApiModelProperty(value = "公开报名", required = true)
    private Boolean publicSignUp;
    @ApiModelProperty(value = "公开查询", required = true)
    private Boolean publicShowUp;
    @ApiModelProperty(value = "赛事邀请码", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MatchToken matchToken;
    @ApiModelProperty(value = "创建时间", required = true)
    private Instant createdAt;

    public MatchInfoResponse(Match match) {
        FieldCopier.copy(match, this);
    }
}
