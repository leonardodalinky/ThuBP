package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.request.TokenRequestBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 成为裁判请求.
 * @author Rhacoal
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class BecomeRefereeRequest extends TokenRequestBase {}
