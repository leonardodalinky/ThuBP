package cn.edu.tsinghua.thubp.web.request;


import cn.edu.tsinghua.thubp.common.annotation.AutoModify;
import cn.edu.tsinghua.thubp.common.intf.ModifiableSource;
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
public class UserUpdateRequest implements ModifiableSource {
    @ApiModelProperty(value = "用户名")
    @AutoModify
    private String username;
    @ApiModelProperty(value = "旧密码")
    private String oldPassword;
    @ApiModelProperty(value = "新密码")
    private String newPassword;
    @ApiModelProperty(value = "头像的文件名(key)")
    @Pattern(regexp = "^"+ IUploadType.STR_AVATAR + "_\\d+_[a-zA-Z0-9.-]+$")
    private String avatar;
    @ApiModelProperty(value = "性别")
    @AutoModify
    private Gender gender;
    @ApiModelProperty(value = "手机")
    @Pattern(regexp = "^\\d{11}$")
    @AutoModify
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    @Email
    @AutoModify
    private String email;
}
