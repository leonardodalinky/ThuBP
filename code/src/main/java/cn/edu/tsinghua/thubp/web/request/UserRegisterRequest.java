package cn.edu.tsinghua.thubp.web.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author Link
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Pattern(regexp = "^\\d{11}$")
    private String mobile;
    @Email
    private String email;
    /**
     * 清华服务授权码
     */
    @NotBlank
    private String code;
}
