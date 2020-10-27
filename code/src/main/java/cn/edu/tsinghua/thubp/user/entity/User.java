package cn.edu.tsinghua.thubp.user.entity;

import cn.edu.tsinghua.thubp.user.enums.RoleType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User extends AuditBase {

    @Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String username;
    @NonNull
    private String password;
    @NonNull
    private Boolean enabled;
    @NonNull
    private RoleType role;
    @NonNull
    private String mobile;
    private String email;

    public List<SimpleGrantedAuthority> getRoles() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }
}
