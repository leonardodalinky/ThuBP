package cn.edu.tsinghua.thubp.match.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.common.intf.ModifiableTarget;
import cn.edu.tsinghua.thubp.match.enums.GameStatus;
import cn.edu.tsinghua.thubp.plugin.GameResult;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * 真正的一场比赛，只准有两个参赛单位
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "game")
public class Game extends AuditBase implements ModifiableTarget {
    @Transient
    public static final String SEQUENCE_NAME = "game_sequence";

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String gameId;
    /**
     * 单场比赛状态
     */
    @lombok.NonNull
    private GameStatus status;
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
    /**
     * 记分板内容
     */
    @JsonSerialize(using = JsonValueSerializer.class)
    @org.jetbrains.annotations.Nullable
    private GameResult result;
    /**
     * 开始时间
     */
    @org.jetbrains.annotations.Nullable
    private Instant startTime;
    /**
     * 地点
     */
    @org.jetbrains.annotations.Nullable
    private String location;
    // TODO: 单场比赛中的其他信息
}
