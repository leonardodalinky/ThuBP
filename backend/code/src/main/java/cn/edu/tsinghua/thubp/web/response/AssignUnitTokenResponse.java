package cn.edu.tsinghua.thubp.web.response;

import cn.edu.tsinghua.thubp.common.response.TokenResponseBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class AssignUnitTokenResponse extends TokenResponseBase {}