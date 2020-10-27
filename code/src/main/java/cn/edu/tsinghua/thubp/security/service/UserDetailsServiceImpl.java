package cn.edu.tsinghua.thubp.security.service;

import cn.edu.tsinghua.thubp.security.entity.JwtUser;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


/**
 * UserDetailsService实现类
 * @author Link
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String name) {
        User user = userService.find(name);
        return new JwtUser(user);
    }

}
