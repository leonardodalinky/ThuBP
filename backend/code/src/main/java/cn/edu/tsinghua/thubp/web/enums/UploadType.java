package cn.edu.tsinghua.thubp.web.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传的类型
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UploadType implements IUploadType{

    AVATAR(STR_AVATAR, "用户头像"),
    MATCH_PREVIEW(STR_MATCH_PREVIEW, "比赛预览图");

    String name;
    String description;
}
