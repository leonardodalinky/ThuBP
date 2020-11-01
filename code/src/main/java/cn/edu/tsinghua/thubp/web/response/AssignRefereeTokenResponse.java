package cn.edu.tsinghua.thubp.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRefereeTokenResponse extends SimpleResponse {
    private String refereeToken;
    private long expirationTime;
}
