package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class AdminController {
    private final UserService userService;

    @ResponseBody
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
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
}
