package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.notification.entity.Notification;
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
public class NotificationUnreadResponse extends SimpleResponse {
    @ApiModelProperty(value = "未读通知数量", required = true)
    private String unread;
}