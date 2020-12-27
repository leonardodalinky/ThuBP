package cn.edu.tsinghua.thubp.security.service;

import cn.edu.tsinghua.thubp.security.entity.JwtUser;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    /**
     * 此处 UserName 是 thuId
     * @param thuId thuId
     */
    @Override
    public UserDetails loadUserByUsername(String thuId){
        User user = userService.findByThuId(thuId);
        return new JwtUser(user);
    }

    public UserDetails loadUserByUserId(String userId) {
        User user = userService.findByUserId(userId);
        return new JwtUser(user);
    }
}
