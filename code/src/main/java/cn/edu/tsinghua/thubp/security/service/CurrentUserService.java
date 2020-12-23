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

    /**
     * 获得当前的用户的全部信息
     * @apiNote 此操作需要额外进行一次数据库查询
     * @return 当前 User
     */
    public User getUser() {
        JwtUser jwtUser = (JwtUser)authenticationFacade.getAuthentication().getPrincipal();
        return userService.findByUserId(jwtUser.getUserId());
    }

    /**
     * 获得当前的用户的 ID
     * @apiNote 此操作无需额外数据库查询
     * @return 当前用户 ID
     */
    public String getUserId() {
        if (authenticationFacade.getAuthentication() == null || authenticationFacade.getAuthentication().getPrincipal() == null) {
            return "-1";
        }
        return ((JwtUser)authenticationFacade.getAuthentication().getPrincipal()).getUserId();
    }
}
