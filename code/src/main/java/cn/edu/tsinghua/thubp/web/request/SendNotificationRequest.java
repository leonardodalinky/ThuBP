package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

/**
 * 创建比赛的请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationRequest {
    @ApiModelProperty(value = "通知标题", required = true)
    @NotBlank
    private String title;
    @ApiModelProperty(value = "通知内容", required = true)
    @NotBlank
    private String content;
}
