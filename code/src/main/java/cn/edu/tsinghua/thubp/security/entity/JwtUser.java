package cn.edu.tsinghua.thubp.security.entity;

import cn.edu.tsinghua.thubp.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * JWT用户对象
 * @author Link
 */
@NoArgsConstructor
@AllArgsConstructor
public class JwtUser implements UserDetails {

    private String id;
    private String username;
    private String password;
    private Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 通过 user 对象创建jwtUser
     */
    public JwtUser(User user) {
        id = user.getId();
        username = user.getUsername();
        password = user.getPassword();
        enabled = user.getEnabled() == null || user.getEnabled();
        authorities = user.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String toString() {
        return "JwtUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", authorities=" + authorities +
                '}';
    }

}
