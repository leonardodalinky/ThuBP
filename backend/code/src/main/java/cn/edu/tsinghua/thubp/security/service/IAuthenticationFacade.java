package cn.edu.tsinghua.thubp.security.service;

import org.springframework.security.core.Authentication;

/**
 * 用于获取当前已登录用户的模块
 * @author Link
 */
public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
