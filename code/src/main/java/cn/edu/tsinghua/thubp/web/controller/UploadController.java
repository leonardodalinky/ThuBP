package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.enums.UploadType;
import cn.edu.tsinghua.thubp.web.request.UploadRequest;
import cn.edu.tsinghua.thubp.web.response.UploadResponse;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class UploadController {
    private final CurrentUserService currentUserService;

    /**
     * 客户端向服务端索要一个上传凭证
     */
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
        Auth auth = Auth.create(WebConstant.QINIUYUN_ACCESS_KEY, WebConstant.QINIUYUN_SECRET_KEY);
        String uploadToken = auth.uploadToken(WebConstant.QINIUYUN_BUCKET_NAME, key);
        return new UploadResponse(uploadToken, key);
    }
}
