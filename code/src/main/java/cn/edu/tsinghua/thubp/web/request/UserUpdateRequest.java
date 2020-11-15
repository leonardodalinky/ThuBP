package cn.edu.tsinghua.thubp.web.request;


import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.web.enums.IUploadType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


/**
 * @author Link
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "旧密码")
    private String oldPassword;
    @ApiModelProperty(value = "新密码")
    private String newPassword;
    @ApiModelProperty(value = "头像的文件名(key)")
    @Pattern(regexp = "^"+ IUploadType.STR_AVATAR + "-[a-zA-Z0-9.-]+$")
    private String avatar;
    @ApiModelProperty(value = "性别")
    private Gender gender;
    @ApiModelProperty(value = "手机")
    @Pattern(regexp = "^\\d{11}$")
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    @Email
    private String email;
}
