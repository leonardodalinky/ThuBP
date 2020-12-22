package cn.edu.tsinghua.thubp.user.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.user.enums.ThuIdentityType;
import cn.edu.tsinghua.thubp.user.exception.UserErrorCode;
import cn.edu.tsinghua.thubp.user.misc.ThuAuthResult;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import com.google.common.collect.ImmutableMap;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@PropertySource(value = {"classpath:config/config-template.properties", "classpath:config/config.properties"},
        ignoreResourceNotFound = true)
public class ThuAuthService {
    private static final long MAX_LENGTH = 1024;
    public static final String TICKET = "ticket";
    public static final String TYPE = "type";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${thuauth.appid}")
    private String appId;
    @Value("${thuauth.loginurl}")
    private String loginUrl;

    /**
     * 根据 ticket 获取用户身份信息.
     *
     * @param userIpAddr 用户 IP. 应该用 _ 替代 .
     * @param ticket     身份验证票据
     * @return 身份信息
     */
    public ThuAuthResult getThuIdentity(String userIpAddr, String ticket) {
        String result = restTemplate.getForEntity(
                loginUrl,
                String.class,
                ImmutableMap.of(
                        "AppID", appId,
                        "ticket", ticket,
                        "UserIpAddr", userIpAddr
                )).getBody();
        if (result == null || result.length() > 1024) {
            throw new CommonException(UserErrorCode.THUAUTH_RESPONSE_NOT_VALID, ImmutableMap.of(TICKET, ticket));
        }
        String[] values = result.split(":");
        ThuAuthResult.ThuAuthResultBuilder builder = ThuAuthResult.builder();
        for (String value : values) {
            String[] kv = value.split("=", 2);
            if (kv.length != 2) {
                throw new CommonException(UserErrorCode.THUAUTH_RESPONSE_NOT_VALID, ImmutableMap.of(TICKET, ticket));
            }
            switch (kv[0]) {
                case "code":
                    if (!Objects.equals(kv[1], "0")) {
                        throw new CommonException(UserErrorCode.THUAUTH_RESPONSE_NOT_VALID, ImmutableMap.of(TICKET, ticket));
                    }
                    break;
                case "zjh":
                    builder.thuId(kv[1]);
                    break;
                case "xm":
                    builder.realName(kv[1]);
                    break;
                case "yhlb":
                    switch (kv[1]) {
                        case "J0000":
                        case "H0000":
                        case "J0054":
                            builder.identityType(ThuIdentityType.TEACHER);
                            break;
                        case "X0011":
                        case "X0021":
                        case "X0031":
                            builder.identityType(ThuIdentityType.STUDENT);
                            break;
                        default:
                            throw new CommonException(UserErrorCode.THUAUTH_USER_IDENTITY_NOT_VALID, ImmutableMap.of(TYPE, kv[1]));
                    }
                    break;
                case "email":
                    builder.email(kv[1]);
                    break;
            }
        }
        try {
            return builder.build();
        } catch (Exception exception) {
            throw new CommonException(UserErrorCode.THUAUTH_RESPONSE_NOT_VALID, ImmutableMap.of(TICKET, ticket));
        }
    }
}
