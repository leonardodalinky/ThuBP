package cn.edu.tsinghua.thubp.web.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangePwdRequest {
    @ApiModelProperty(value = "用户 ID", required = true)
    @NotNull
    String userId;
    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank
    String newPassword;
}
