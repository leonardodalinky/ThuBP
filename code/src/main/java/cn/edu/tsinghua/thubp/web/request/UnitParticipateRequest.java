package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.common.request.TokenRequestBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitParticipateRequest extends TokenRequestBase {}
