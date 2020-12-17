package cn.edu.tsinghua.thubp.match.entity;

import cn.edu.tsinghua.thubp.comment.intf.Commentable;
import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import cn.edu.tsinghua.thubp.common.intf.ModifiableTarget;
import cn.edu.tsinghua.thubp.match.enums.MatchStatus;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "match")
public class Match extends AuditBase implements ModifiableTarget, Commentable {
    /**
     * 用于给每个 match 赋值 matchId 的静态常量
     */
    @Transient
    public static final String SEQUENCE_NAME = "match_sequence";

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String matchId;
    /**
     * 是否活跃中（归档)
     * 不展示于用户
     */
    @lombok.NonNull
    private Boolean active;
    /**
     * 赛事的状态
     */
    @lombok.NonNull
    @Builder.Default
    private MatchStatus status = MatchStatus.PREPARE;
    /**
     * 赛事发起者/组织者
     */
    @lombok.NonNull
    private String organizerUserId;
    /**
     * 赛事名字
     */
    @lombok.NonNull
    private String name;
    /**
     * 赛事描述
     */
    @lombok.NonNull
    private String description;
    /**
     * 是否公开展示
     */
    @lombok.NonNull
    private Boolean publicShowUp;
    /**
     * 面向人群
     */
    private String targetGroup;
    /**
     * 赛事开始时间
     */
    @org.jetbrains.annotations.Nullable
    private Instant startTime;
    /**
     * 赛事预览图
     */
    @org.jetbrains.annotations.Nullable
    private URL preview;
    /**
     * 赛事预览大图
     */
    @org.jetbrains.annotations.Nullable
    private URL previewLarge;
    @lombok.NonNull
    private String matchTypeId;
    /**
     * 参赛者的 userId 列表
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private List<String> participants = new ArrayList<>();
    /**
     * 裁判的 userId 列表
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private List<String> referees = new ArrayList<>();
    /**
     * 裁判邀请码
     */
    @org.jetbrains.annotations.Nullable
    private RefereeToken refereeToken;
    /**
     * 参赛单位的 unitId 列表
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private List<String> units = new ArrayList<>();
    /**
     * 参赛单位有效最小人数
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private Integer minUnitMember = 1;
    /**
     * 参赛单位最大人数
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private Integer maxUnitMember = 99;
    /**
     * 是否开放报名创建参赛单位
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private Boolean publicSignUp = true;
    /**
     * 若未公开报名，则需要邀请码才能注册参赛单位
     */
    @org.jetbrains.annotations.Nullable
    private MatchToken matchToken;
    /**
     * 赛事中的赛程（轮）
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private List<String> rounds = new ArrayList<>();
//   这部分暂且不加入，之后有需求再添加
//    /**
//     * 赛事的默认记分板 ID
//     */
//    @org.jetbrains.annotations.Nullable
//    private String defaultScoreboardTypeId;
//    /**
//     * 赛事的默认记分板配置
//     */
//    @org.jetbrains.annotations.Nullable
//    private JsonNode defaultScoreboardConfig;
    /**
     * 评论的 ID
     */
    @lombok.NonNull
    @lombok.Builder.Default
    private List<String> comments = new ArrayList<>();
}
