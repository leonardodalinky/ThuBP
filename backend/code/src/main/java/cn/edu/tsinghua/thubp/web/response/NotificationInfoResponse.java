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
public class NotificationInfoResponse extends SimpleResponse {
    @ApiModelProperty(value = "个人通知总数量", required = true)
    Integer total;
    @ApiModelProperty(value = "个人收到的通知", required = true)
    List<Notification> notifications;
}
