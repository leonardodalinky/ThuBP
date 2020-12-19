package cn.edu.tsinghua.thubp.bulletin.entity;

import cn.edu.tsinghua.thubp.common.entity.AuditBase;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bulletin")
public class BulletinEntry extends AuditBase {
    @Id
    private ObjectId id;
    /**
     * 热度值
     */
    @NonNull
    private Integer hotValue;
    /**
     * 赛事 ID
     */
    @NonNull
    private String matchId;
}
