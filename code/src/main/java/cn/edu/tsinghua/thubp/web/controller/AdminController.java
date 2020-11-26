package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import cn.edu.tsinghua.thubp.notification.service.NotificationService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.enums.ThuIdentityType;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.SendNotificationRequest;
import cn.edu.tsinghua.thubp.web.response.SendNotificationResponse;
import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class AdminController {
    private final UserService userService;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final NotificationService notificationService;

    @ApiOperation(value = "获得所有的用户（管理员）", tags = {SwaggerTagUtil.ADMIN, SwaggerTagUtil.ROOT})
    @ResponseBody
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public List<Object> users() {
        List<User> users = userService.getAll();
        List<Object> ret = new ArrayList<>();
        for (User user: users) {
            ret.add(ImmutableMap.builder()
                    .put("userId", user.getUserId())
                    .put("username", user.getUsername())
                    .put("thuId", user.getThuId())
                    .build()
            );
        }
        return ret;
    }

    @ApiOperation(value = "重置数据库", tags = {SwaggerTagUtil.ROOT})
    @ResponseBody
    @RequestMapping(value = "/admin/reset", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROOT')")
    public SimpleResponse reset() {
        // 重置数据库
        log.info("清空数据库");
        for (String name: mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(name);
        }
        // 初始化一个 admin 用户
        log.info("创建 root 用户");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!userRepository.existsByThuId("2018000000")) {
            User user = User.builder()
                    .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                    .gender(Gender.UNKNOWN)
                    .enabled(true)
                    .username("root")
                    .realName("根")
                    .thuIdentityType(ThuIdentityType.EXTERNAL)
                    .password(bCryptPasswordEncoder.encode("root"))
                    .role(RoleType.ROOT)
                    .mobile("10000000000")
                    .email("thubp@tsinghua.edu.cn")
                    .thuId("2018000000")
                    .unreadNotificationCount(0)
                    .build();
            userRepository.save(user);
        }
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "发送系统通知", tags = {SwaggerTagUtil.ROOT})
    @ApiImplicitParams(
            @ApiImplicitParam(name = "userId", value = "用户 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/admin/{userId}/notification", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROOT')")
    public SendNotificationResponse sendNotification(@PathVariable String userId,
                                           @RequestBody @Valid SendNotificationRequest sendNotificationRequest) {
        String notificationId = notificationService.sendNotificationFromSystem(
                userId,
                sendNotificationRequest.getTitle(),
                sendNotificationRequest.getContent(),
                NotificationTag.NORMAL
        );
        return new SendNotificationResponse(notificationId);
    }
}
