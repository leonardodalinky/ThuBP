package cn.edu.tsinghua.thubp.match.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.common.intf.ModifiableTarget;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * PUnit 代表 Participating Unit，即 参赛单位
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "unit")
public class Unit extends AuditBase implements ModifiableTarget {
    @Transient
    public static final String SEQUENCE_NAME = "unit_sequence";

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String unitId;
    /**
     * 参赛单位名字，通常用于小组
     */
    @lombok.NonNull
    private String name;

    /**
     * 创建者的 userId
     */
    @lombok.NonNull
    private String creatorId;
    /**
     * 成员的 userId 列表（包括创建者）
     */
    @lombok.NonNull
    private List<String> members;
    /**
     * 所属比赛的 ID
     */
    @lombok.NonNull
    private String matchId;
    /**
     * 加入参赛单位的邀请码
     */
    @org.jetbrains.annotations.Nullable
    private UnitToken unitToken;
}
