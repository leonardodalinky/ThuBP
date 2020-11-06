package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.common.config.GlobalConfig;
import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.enums.UploadType;
import cn.edu.tsinghua.thubp.web.request.UploadRequest;
import cn.edu.tsinghua.thubp.web.response.UploadResponse;
import com.qiniu.util.Auth;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class UploadController {
    private final CurrentUserService currentUserService;
    private final GlobalConfig globalConfig;

    /**
     * 客户端向服务端索要一个上传凭证
     */
    @ApiOperation(value = "获取上传凭证", tags = SwaggerTagUtil.UPLOAD)
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public UploadResponse upload(@RequestBody @Valid UploadRequest uploadRequest) {
        UploadType uploadType = uploadRequest.getUploadType();
        // 此处可以限制一个人不能短时间传太多次？但现阶段不加入。
        User nowUser = currentUserService.getUser();
        // 生成文件名
        String key = uploadType.getName() +
                "_" +
                nowUser.getUserId() +
                "_" + new Date().getTime() +
                ((Objects.isNull(uploadRequest.getSuffix()))? "" : uploadRequest.getSuffix());
        Auth auth = Auth.create(globalConfig.getQiNiuAccessKey(), globalConfig.getQiNiuPrivateKey());
        String uploadToken = auth.uploadToken(globalConfig.getQiNiuBucketName(), key);
        return new UploadResponse(uploadToken, key);
    }
}
