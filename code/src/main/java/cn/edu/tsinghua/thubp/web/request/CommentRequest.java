package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

/**
 * 创建评论的通用请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @ApiModelProperty(value = "评论内容", required = true)
    @NotNull
    private String content;
    @ApiModelProperty(value = "回复的评论 ID")
    @Nullable
    private String replyId;
}
