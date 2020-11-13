package cn.edu.tsinghua.thubp.user.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.enums.ThuIdentityType;
import lombok.*;
import org.bson.types.ObjectId;
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
     * 用于给每个 user 赋值 userId 的静态常量
     */
    @Transient
    public static final String SEQUENCE_NAME = "user_sequence";

    @Id
    private ObjectId id;
    /**
     * 清华证件号
     */
    @NonNull
    @Indexed(unique = true)
    private String thuId;
    /**
     * 真实姓名
     */
    @NonNull
    private String realName;
    /**
     * 清华用户身份类别
     */
    @NonNull
    private ThuIdentityType thuIdentityType;
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
    @org.jetbrains.annotations.Nullable
    private String mobile;

    @Indexed(unique = true)
    @org.jetbrains.annotations.Nullable
    private String email;

    @org.jetbrains.annotations.Nullable
    private List<String> organizedMatches;

    @org.jetbrains.annotations.Nullable
    private List<String> participatedMatches;

    @org.jetbrains.annotations.Nullable
    private List<String> participatedUnits;

    public List<SimpleGrantedAuthority> getRoles() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }
}
