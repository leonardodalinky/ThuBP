package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 修改评论的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentModifyRequest {
    @ApiModelProperty(value = "评论内容", required = true)
    @NotNull
    private String content;
}
