package cn.edu.tsinghua.thubp.web.request;


import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "用户名", required = true)
    @javax.validation.constraints.NotBlank
    private String username;
    @ApiModelProperty(value = "密码", required = true)
    @javax.validation.constraints.NotBlank
    private String password;
    @ApiModelProperty(value = "手机")
    @javax.validation.constraints.Pattern(regexp = "^\\d{11}$")
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    @javax.validation.constraints.Email
    private String email;
    @ApiModelProperty(value = "个人描述")
    @javax.validation.constraints.Size(max = 1000)
    private String description;
    /**
     * 清华服务授权码
     */
    @ApiModelProperty(value = "清华服务授权码", required = true)
    @javax.validation.constraints.NotBlank
    private String ticket;
}
