package cn.edu.tsinghua.thubp.security.config;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.exception.JwtAccessDeniedHandler;
import cn.edu.tsinghua.thubp.security.exception.JwtAuthenticationEntryPoint;
import cn.edu.tsinghua.thubp.security.filter.JwtAuthenticationFilter;
import cn.edu.tsinghua.thubp.security.filter.JwtAuthorizationFilter;
import cn.edu.tsinghua.thubp.security.service.UserDetailsServiceImpl;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * Spring Security配置类
 * @author Link
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 设置自定义的userDetailsService以及密码编码器
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                // 禁用 CSRF
                .csrf().disable()
                .authorizeRequests()
                // 指定路径下的资源需要验证了的用户才能访问
                .antMatchers(HttpMethod.POST, SecurityConstant.AUTH_LOGIN_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstant.AUTH_REGISTER_URL).permitAll()
                .antMatchers("/api/v1/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole(RoleType.ADMIN.getName())
                // 其他都放行了
                .anyRequest().permitAll()
                .and()
                //添加自定义Filter
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailsServiceImpl))
                // 不需要session（不创建会话）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 授权异常处理
                .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler());
        // 防止H2 web 页面的Frame 被拦截
        http.headers().frameOptions().disable();
    }

}
