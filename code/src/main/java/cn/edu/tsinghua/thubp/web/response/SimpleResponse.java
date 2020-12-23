package cn.edu.tsinghua.thubp.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class SimpleResponse {
    public static final String OK = "ok";
    @ApiModelProperty(value = "简短成功讯息", required = true)
    @Builder.Default
    private String message = OK;

    public SimpleResponse() {
        this.message = OK;
    }
}
