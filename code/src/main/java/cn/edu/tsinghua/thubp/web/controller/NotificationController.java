package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.notification.entity.Notification;
import cn.edu.tsinghua.thubp.notification.repository.NotificationRepository;
import cn.edu.tsinghua.thubp.notification.service.NotificationService;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.NotificationDeleteRequest;
import cn.edu.tsinghua.thubp.web.response.NotificationInfoResponse;
import cn.edu.tsinghua.thubp.web.response.NotificationUnreadResponse;
import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class NotificationController {
    private final CurrentUserService currentUserService;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @ApiOperation(value = "查看自身通知", tags = SwaggerTagUtil.NOTIFICATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页面", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/notification", method = RequestMethod.GET)
    public NotificationInfoResponse info(@RequestParam(value = "page", defaultValue = "0") String page,
                                         @RequestParam(value = "pageSize", defaultValue = "10") String pageSize) {
        User user = currentUserService.getUser();
        return new NotificationInfoResponse(
                notificationRepository.findAllByNotificationIdIn(
                        user.getNotifications(),
                        PageRequest.of(Integer.parseInt(page), Integer.parseInt(pageSize)
                        )
                )
        );
    }

    @ApiOperation(value = "获得未读通知数量", tags = SwaggerTagUtil.NOTIFICATION)
    @ResponseBody
    @RequestMapping(value = "/notification/unread", method = RequestMethod.GET)
    public NotificationUnreadResponse unread() {
        String userId = currentUserService.getUserId();
        return new NotificationUnreadResponse(notificationService.getUnreadCount(userId));
    }

    @ApiOperation(value = "单个通知标为已读", tags = SwaggerTagUtil.NOTIFICATION)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "notificationId", value = "通知 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/notification/{notificationId}", method = RequestMethod.POST)
    public SimpleResponse single(@PathVariable String notificationId) {
        String userId = currentUserService.getUserId();
        notificationService.readNotification(userId, notificationId);
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "全部通知标为已读", tags = SwaggerTagUtil.NOTIFICATION)
    @ResponseBody
    @RequestMapping(value = "/notification", method = RequestMethod.POST)
    public SimpleResponse all() {
        String userId = currentUserService.getUserId();
        notificationService.readAllNotification(userId);
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "删除通知", tags = SwaggerTagUtil.NOTIFICATION)
    @ResponseBody
    @RequestMapping(value = "/notification", method = RequestMethod.DELETE)
    public SimpleResponse delete(@RequestBody @Valid NotificationDeleteRequest notificationDeleteRequest) {
        String userId = currentUserService.getUserId();
        notificationService.deleteNotifications(userId, notificationDeleteRequest.getNotifications());
        return new SimpleResponse(SimpleResponse.OK);
    }
}
