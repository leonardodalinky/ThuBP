package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.request.TokenRequestBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class UnitParticipateRequest extends TokenRequestBase {}
