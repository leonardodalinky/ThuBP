package cn.edu.tsinghua.thubp.user.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.omg.CORBA.UNKNOWN;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Gender {
    MALE("MALE", "男"),
    FEMALE("FEMALE", "女"),
    OTHER("OTHER", "其他"),
    UNKNOWN("UNKNOWN", "未知");

    String name;
    String description;
}
