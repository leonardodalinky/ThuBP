package cn.edu.tsinghua.thubp.web.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


/**
 * @author Link
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @NotBlank
    private String username;
    private String password;
    private String fullName;
    private Boolean enabled;
}
