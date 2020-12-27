package cn.edu.tsinghua.thubp.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户登录请求 DTO(Data Transfer Object)
 * @author Link
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String thuId;
    private String password;
    private Boolean rememberMe;
}
