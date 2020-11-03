package cn.edu.tsinghua.thubp.web.request;


import cn.edu.tsinghua.thubp.user.enums.Gender;
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
    private String username;
    private String oldPassword;
    private String newPassword;
    private Gender gender;
    @Pattern(regexp = "^\\d{11}$")
    private String mobile;
    @Email
    private String email;
}
