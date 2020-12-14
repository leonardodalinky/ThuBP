package cn.edu.tsinghua.thubp.match.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.match.enums.RoundStatus;
import lombok.*;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 赛事中的一轮
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "round")
public class Round extends AuditBase {
    @Transient
    public static final String SEQUENCE_NAME = "round_sequence";

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String roundId;
    /**
     * 轮次的名字
     */
    @org.jetbrains.annotations.Nullable
    private String name;
    /**
     * 轮次的描述
     */
    @org.jetbrains.annotations.Nullable
    private String description;
    /**
     * 轮次的标签
     */
    @org.jetbrains.annotations.Nullable
    private String tag;
    /**
     * 本轮的状态
     */
    @lombok.NonNull
    private RoundStatus status;
    /**
     * 参赛单位的 unitId 列表
     */
    @lombok.NonNull
    private List<String> units;
    /**
     * 所有单场比赛的 gameId 列表
     */
    @lombok.NonNull
    private List<String> games;
}
