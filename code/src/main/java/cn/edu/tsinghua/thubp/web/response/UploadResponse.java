package cn.edu.tsinghua.thubp.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    /**
     * 上传凭证
     */
    private String uploadToken;
    /**
     * 待存储的资源名，客户端需要以这个为名字
     */
    private String key;
}
