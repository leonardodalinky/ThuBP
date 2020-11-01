package cn.edu.tsinghua.thubp.web.request;

import cn.edu.tsinghua.thubp.web.enums.UploadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest {
    @javax.validation.constraints.NotNull
    private UploadType uploadType;
    /**
     * 文件名的后缀
     */
    private String suffix;
}
