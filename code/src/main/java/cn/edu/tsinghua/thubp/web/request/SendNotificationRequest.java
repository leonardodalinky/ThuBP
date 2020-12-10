package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

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
    @ApiModelProperty(value = "通知类型", required = true)
    @NotNull
    private NotificationTag tag;
    @ApiModelProperty(value = "额外内容", required = false)
    @Nullable
    private Map<String, Object> extra;
}
