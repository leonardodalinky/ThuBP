package cn.edu.tsinghua.thubp.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse extends SimpleResponse {
    /**
     * 上传凭证
     */
    @ApiModelProperty(value = "上传凭证", required = true)
    private String uploadToken;
    /**
     * 待存储的资源名，客户端需要以这个为名字
     */
    @ApiModelProperty(value = "上传的文件名", required = true)
    private String key;
}
