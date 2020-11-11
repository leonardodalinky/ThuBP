package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.request.TokenRequestBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 成为裁判请求.
 * @author Rhacoal
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BecomeRefereeRequest extends TokenRequestBase {}
