package cn.edu.tsinghua.thubp.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static cn.edu.tsinghua.thubp.web.constant.WebConstant.URL_PREFIX_API_V1;

/**
 * Spring Security相关配置常量
 * @author Link
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstant {
    /**
     * 登录的地址
     */
    public static final String AUTH_LOGIN_URL = URL_PREFIX_API_V1 + "/auth/login";
    /**
     * 登录的地址
     */
    public static final String AUTH_REGISTER_URL = URL_PREFIX_API_V1 + "/auth/register";
    /**
     * 检查用户名存在性的地址
     */
    public static final String AUTH_CHECK_USERNAME_URL = URL_PREFIX_API_V1 + "/user/check";
    /**
     * 角色的key
     **/
    public static final String ROLE_CLAIMS = "rol";
    /**
     * rememberMe 为 false 的时候过期时间是1个小时
     */
    public static final long EXPIRATION = 60 * 60L;
    /**
     * rememberMe 为 true 的时候过期时间是7天
     */
    public static final long EXPIRATION_REMEMBER = 60 * 60 * 24 * 7L;
    /**
     * JWT签名密钥硬编码到应用程序代码中。
     */
    public static final String JWT_SECRET_KEY = "6L+Z5piv5oiR55qE5Liq5Lq65a+G6ZKl77yM5bCx6Zeu5L2g5pyN5LiN5pyN77yf";
    /**
     * JWT 的 Issuer
     */
    public static final String JWT_ISSUER = "ThuQH";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
}
