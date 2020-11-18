package cn.edu.tsinghua.thubp.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends SimpleResponse {
    @ApiModelProperty(value = "评论 ID", required = true)
    private String commentId;
}
