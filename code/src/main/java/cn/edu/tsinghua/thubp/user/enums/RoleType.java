package cn.edu.tsinghua.thubp.user.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum RoleType {
    USER("USER", "用户"),
    ADMIN("ADMIN", "Admin");
    String name;
    String description;

    RoleType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Optional<RoleType> fromName(String name) {
        for (RoleType s: RoleType.values()) {
            if (s.name.equals(name)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
