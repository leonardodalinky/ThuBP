package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.UserRegisterRequest;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
import cn.edu.tsinghua.thubp.web.response.UserInfoResponse;
import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
import cn.edu.tsinghua.thubp.web.response.UserRegisterResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class UserController {

    private final UserService userService;
    /**
     * 这个用于获得当前用户
     */
    private final CurrentUserService currentUserService;
    @Value("${spring.profiles.active}")
    private String profile;

    /*
    * Login 的方法在 Security 里面的 Filter 中被实现，地址为 /api/v1/auth/login，方法为 POST
    * 到达此处的，都已经登陆成功
    * */
    @ApiOperation(value = "用户登录", tags = SwaggerTagUtil.ROLECHECK)
    @ResponseBody
    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public SimpleResponse login() {
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "注册账户", tags = SwaggerTagUtil.ROLECHECK)
    @ResponseBody
    @RequestMapping(value = "/auth/register", method = RequestMethod.POST)
    public UserRegisterResponse register(HttpServletRequest request, @RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        if (Objects.equals(profile, "dev")) {
            String userId = userService.saveLegacy(userRegisterRequest);
            return new UserRegisterResponse(userId);
        } else {
            String userId = userService.save(request.getRemoteAddr().replace(".", "_"), userRegisterRequest);
            return new UserRegisterResponse(userId);
        }
    }

    @ApiOperation(value = "个人信息", tags = SwaggerTagUtil.USERINFO)
    @ResponseBody
    @RequestMapping(value = "/user/info", method = RequestMethod.GET)
    public UserInfoResponse infoGet() {
        User user = currentUserService.getUser();
        return new UserInfoResponse(user);
    }

    @ApiOperation(value = "修改个人信息", tags = SwaggerTagUtil.USERINFO)
    @ResponseBody
    @RequestMapping(value = "/user/info", method = RequestMethod.POST)
    public SimpleResponse infoPost(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        User user = currentUserService.getUser();
        userService.update(user, userUpdateRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }
}
