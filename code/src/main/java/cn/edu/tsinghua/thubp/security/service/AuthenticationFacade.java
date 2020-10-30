package cn.edu.tsinghua.thubp.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用于获取当前已登录用户的模块
 * @author Link
 */
@Component
public class AuthenticationFacade implements IAuthenticationFacade {
    /**
     *
     * @return 返回的 Authentication 中，Principal 为 JWTUser
     */
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
