package cn.edu.tsinghua.thubp.match.entity;
import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 真正的一场比赛，只准有两个参赛单位
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "game")
public class Game extends AuditBase {
    @Transient
    public static final String SEQUENCE_NAME = "game_sequence";

    public Game(String unit0, @org.jetbrains.annotations.Nullable String unit1) {
        this.unit0 = unit0;
        this.unit1 = unit1;
    }

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String gameId;
    /**
     * 单场比赛状态
     */
    @lombok.NonNull
    GameStatus status;
    /**
     * 参赛单位 0
     */
    @lombok.NonNull
    private String unit0;
    /**
     * 参赛单位 1
     */
    @org.jetbrains.annotations.Nullable
    private String unit1;
    // TODO: 单场比赛中的其他信息
}
