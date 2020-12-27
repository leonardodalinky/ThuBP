package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.web.enums.UploadType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest {
    @ApiModelProperty(value = "上传类型", required = true)
    @javax.validation.constraints.NotNull
    private UploadType uploadType;
    /**
     * 文件名的后缀
     */
    @ApiModelProperty(value = "文件名后缀", example = ".png")
    private String suffix;
}
