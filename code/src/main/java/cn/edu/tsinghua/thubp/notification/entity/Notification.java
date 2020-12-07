package cn.edu.tsinghua.thubp.notification.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * 系统通知
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notification")
public class Notification {
    @Transient
    @JsonIgnore
    public static final String SEQUENCE_NAME = "notification_sequence";

    @ApiModelProperty(value = "通知 Id", required = true)
    @NonNull
    @Indexed(unique = true)
    private String notificationId;
    @ApiModelProperty(value = "发送者 Id", required = true)
    @NonNull
    private String fromUserId;
    @ApiModelProperty(value = "接收者 Id", required = true)
    @NonNull
    private String toUserId;
    @ApiModelProperty(value = "通知类型", required = true)
    @NonNull
    private NotificationTag tag;
    @ApiModelProperty(value = "通知标题", required = true)
    @NonNull
    private String title;
    @ApiModelProperty(value = "通知内容", required = true)
    @NonNull
    private String content;
    @ApiModelProperty(value = "是否已读", required = true)
    @NonNull
    private Boolean isRead;
    @ApiModelProperty(value = "创建日期", required = true)
    @CreatedDate
    private Instant createdAt;
}
