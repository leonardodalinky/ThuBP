package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    @ApiModelProperty(value = "用户权限", required = true)
    private RoleType role;
    @ApiModelProperty(value = "性别", required = true)
    private Gender gender;
    // 非空的时候才会出现在回复中
    @ApiModelProperty(value = "手机")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String email;

    public UserInfoResponse(User user) {
        this.thuId = user.getThuId();
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.gender = user.getGender();
        this.mobile = user.getMobile();
        this.email = user.getEmail();
    }
}
