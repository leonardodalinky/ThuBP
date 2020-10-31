package cn.edu.tsinghua.thubp.web.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Link
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    @javax.validation.constraints.NotBlank
    private String username;
    @javax.validation.constraints.NotBlank
    private String password;
    @javax.validation.constraints.Pattern(regexp = "^\\d{11}$")
    private String mobile;
    @javax.validation.constraints.Email
    private String email;
    /**
     * 清华服务授权码
     */
    @javax.validation.constraints.NotBlank
    private String code;
}
