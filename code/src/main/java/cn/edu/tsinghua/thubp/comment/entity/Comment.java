package cn.edu.tsinghua.thubp.comment.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import lombok.*;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 评论
 * @author AyajiLin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "comment")
public class Comment extends AuditBase  {
    @Transient
    public static final String SEQUENCE_NAME = "comment_sequence";
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String commentId;
    /**
     * 发起评论人的 ID
     */
    @NonNull
    private String issuerId;
    /**
     * 评论的内容
     */
    @NonNull
    private String content;
    /**
     * 回复的评论 ID
     */
    @Nullable
    private String replyId;
}
