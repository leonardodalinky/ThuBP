package cn.edu.tsinghua.thubp.user.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleType {
    USER("USER", "用户"),
    ADMIN("ADMIN", "管理员"),
    ROOT("ROOT", "上帝"),;

    String name;
    String description;

    public static Optional<RoleType> fromName(String name) {
        for (RoleType s: RoleType.values()) {
            if (s.name.equals(name)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
