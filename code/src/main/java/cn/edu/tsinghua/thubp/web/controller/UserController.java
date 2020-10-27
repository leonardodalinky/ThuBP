package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.security.service.IAuthenticationFacade;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.UserRegisterRequest;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
import cn.edu.tsinghua.thubp.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class UserController {

    private final UserService userService;
    private final IAuthenticationFacade authenticationFacade;

    /*
    * Login 的方法在 Security 里面的 Filter 中被实现，地址为 /api/v1/auth/login，方法为 POST
    * 到达此处的，都已经登陆成功
    * */
    @ResponseBody
    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public BaseResponse login() {
        return new BaseResponse("ok");
    }

    @ResponseBody
    @RequestMapping(value = "/auth/register", method = RequestMethod.POST)
    public BaseResponse register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        userService.save(userRegisterRequest);
        return new BaseResponse("ok");
    }

    @RequestMapping(value = "/admin/update", method = RequestMethod.PATCH)
    @PreAuthorize("hasRole('ADMIN')")
    public void update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        userService.update(userUpdateRequest);
    }

//    @DeleteMapping
//    @RequestMapping(value = "/admin/delete", method = RequestMethod.DELETE)
//    @PreAuthorize("hasRole('ADMIN')")
//    public void deleteUserByUsername(@RequestParam("username") String username) {
//        userService.delete(username);
//    }
}
