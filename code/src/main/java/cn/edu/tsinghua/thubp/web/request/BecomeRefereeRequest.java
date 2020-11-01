package cn.edu.tsinghua.thubp.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成为裁判请求.
 * @author Rhacoal
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BecomeRefereeRequest {
    @javax.validation.constraints.NotBlank
    String refereeToken;
}
