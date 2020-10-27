package cn.edu.tsinghua.thubp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * 负责审计各种修改的基类
 * @author Link
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AuditBase {

    @CreatedDate
//    @Column(updatable = false)
    @JsonIgnore
    private Instant createdAt;

    @LastModifiedDate
    @JsonIgnore
    private Instant updatedAt;

//    @CreatedBy
////    @Column(updatable = false)
//    @JsonIgnore
//    private String createdBy;
//
//    @LastModifiedBy
//    @JsonIgnore
//    private String updatedBy;
}
