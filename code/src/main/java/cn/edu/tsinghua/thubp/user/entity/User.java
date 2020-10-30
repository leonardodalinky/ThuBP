package cn.edu.tsinghua.thubp.user.entity;

import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User extends AuditBase {
    /**
     * 用于给每个 user 赋值 userid 的静态常量
     */
    @Transient
    public static final String SEQUENCE_NAME = "user_sequence";

    @Id
    private String id;
    /**
     * 清华证件号
     */
    @NonNull
    @Indexed(unique = true)
    private String thuId;
    /**
     * 递增的 id， 用于区分各个用户，与证件号无关
     */
    @NonNull
    @Indexed(unique = true)
    private String userId;
    /**
     * 用户昵称
     */
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private Boolean enabled;
    /**
     * 权限等级
     * 目前分为 User 和 Admin 两个等级
     */
    @NonNull
    private RoleType role;
    /**
     * 性别
     */
    @NonNull
    private Gender gender;
    @Indexed(unique = true)
    private String mobile;
    @Indexed(unique = true)
    private String email;

    public List<SimpleGrantedAuthority> getRoles() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }
}
