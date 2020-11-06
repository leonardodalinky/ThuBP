package cn.edu.tsinghua.thubp.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleResponse {
    public static final String OK = "ok";
    @ApiModelProperty(value = "简短成功讯息", required = true)
    private String message;

    public SimpleResponse() {
        this.message = OK;
    }
}
