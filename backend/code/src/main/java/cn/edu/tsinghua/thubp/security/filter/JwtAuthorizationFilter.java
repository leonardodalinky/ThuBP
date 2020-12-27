package cn.edu.tsinghua.thubp.security.filter;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.service.UserDetailsServiceImpl;
import cn.edu.tsinghua.thubp.security.utils.JwtTokenUtils;
import cn.edu.tsinghua.thubp.user.exception.UsernameNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static cn.edu.tsinghua.thubp.security.constant.SecurityConstant.AUTH_LOGIN_URL;

/**
 * 过滤器处理所有HTTP请求，并检查是否存在带有正确令牌的Authorization标头。例如，如果令牌未过期或签名密钥正确。
 * @author Link
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private static final Logger logger = Logger.getLogger(JwtAuthorizationFilter.class.getName());

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        // 登录时界面不 authorize
        if (request.getRequestURI().equals(AUTH_LOGIN_URL)) {
            chain.doFilter(request, response);
            return;
        }
        String token = request.getHeader(SecurityConstant.TOKEN_HEADER);
        if (token == null || !token.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            SecurityContextHolder.clearContext();
        } else {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    /**
     * 获取用户认证信息 Authentication
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String authorization) {
//        log.info("get authentication");
        String token = authorization.replace(SecurityConstant.TOKEN_PREFIX, "");
        try {
            String userId = JwtTokenUtils.getUserIdByToken(token);
//            logger.info("checking userId: " + userId);
            if (!StringUtils.isEmpty(userId)) {
                // 从数据库重新拿了一遍,避免用户的角色信息有变
                UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
                // 密码不需要获取
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                return userDetails.isEnabled() ? usernamePasswordAuthenticationToken : null;
            }
        } catch (UsernameNotFoundException | SignatureException | ExpiredJwtException | MalformedJwtException | IllegalArgumentException exception) {
//            logger.warning("Request to parse JWT with invalid signature. Detail : " + exception.getMessage());
        }
        return null;
    }
}
