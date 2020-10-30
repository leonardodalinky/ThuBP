package cn.edu.tsinghua.thubp.security.service;

import cn.edu.tsinghua.thubp.security.entity.JwtUser;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用来获取当前用户的服务
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrentUserService {
    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;

    public JwtUser getJwtUser() {
        return (JwtUser)authenticationFacade.getAuthentication().getPrincipal();
    }

    public User getUser() {
        JwtUser jwtUser = (JwtUser)authenticationFacade.getAuthentication().getPrincipal();
        return userService.findByUserId(jwtUser.getUserId());
    }
}
