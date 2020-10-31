package cn.edu.tsinghua.thubp.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建赛事请求.
 * @author Rhacoal
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchCreateRequest {
    @javax.validation.constraints.NotBlank
    String name;
    @javax.validation.constraints.NotBlank
    String description;
}
