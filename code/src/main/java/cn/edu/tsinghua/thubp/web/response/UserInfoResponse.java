package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String thuId;
    private String userId;
    private String username;
    private RoleType role;
    private Gender gender;
    // 非空的时候才会出现在回复中
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String mobile;
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
