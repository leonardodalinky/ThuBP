package cn.edu.tsinghua.thubp.match.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "match")
public class RefereeToken {

    @NonNull
    private String tokenId;
    @NonNull
    private Instant expirationTime;

}
