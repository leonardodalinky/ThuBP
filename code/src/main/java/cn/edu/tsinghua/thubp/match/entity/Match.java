package cn.edu.tsinghua.thubp.match.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "match")
public class Match extends AuditBase {
    /**
     * 用于给每个 match 赋值 matchId 的静态常量
     */
    @Transient
    public static final String SEQUENCE_NAME = "match_sequence";

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String matchId;
    @NonNull
    private String organizerUserId;
    @NonNull
    private String name;
    @NonNull
    private String description;

    @org.jetbrains.annotations.Nullable
    private List<String> participants;

}
