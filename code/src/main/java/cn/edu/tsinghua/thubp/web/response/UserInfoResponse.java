package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.common.util.FieldCopier;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URL;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse extends SimpleResponse {
    @ApiModelProperty(value = "清华证件 ID", required = true)
    private String thuId;
    @ApiModelProperty(value = "用户 ID", required = true)
    private String userId;
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @ApiModelProperty(value = "用户头像", required = false)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private URL avatar;
    @ApiModelProperty(value = "用户权限", required = true)
    private RoleType role;
    @ApiModelProperty(value = "性别", required = true)
    private Gender gender;
    @ApiModelProperty(value = "手机")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String email;

    public UserInfoResponse(User user) {
        FieldCopier.copy(user, this);
    }
}