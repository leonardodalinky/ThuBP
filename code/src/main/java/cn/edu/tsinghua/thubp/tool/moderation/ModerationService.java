package cn.edu.tsinghua.thubp.tool.moderation;

import cn.edu.tsinghua.thubp.common.config.GlobalConfig;
import cn.edu.tsinghua.thubp.common.exception.CommonErrorCode;
import cn.edu.tsinghua.thubp.common.exception.CommonException;
import com.google.common.collect.ImmutableMap;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.exception.ClientRequestException;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.moderation.v1.ModerationClient;
import com.huaweicloud.sdk.moderation.v1.model.RunTextModerationRequest;
import com.huaweicloud.sdk.moderation.v1.model.RunTextModerationResponse;
import com.huaweicloud.sdk.moderation.v1.model.TextDetectionItemsReq;
import com.huaweicloud.sdk.moderation.v1.model.TextDetectionReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

/**
 * 文本审查的服务
 */
@Service
public class ModerationService {
    @Autowired
    private ModerationClient moderationClient;

    /**
     * 对 {@code text} 进行文本审核
     * @param text 待进行审核的文本内容
     * @return {@link cn.edu.tsinghua.thubp.tool.moderation.Result}
     */
    public Result moderate(String text) {
        RunTextModerationRequest request = new RunTextModerationRequest().withBody(
                new TextDetectionReq()
                        .withCategories(Arrays.asList("porn","politics", "ad", "abuse", "contraband", "flood"))
                        .withItems(Collections.singletonList(
                                new TextDetectionItemsReq().withType("content").withText(text)
                        ))
        );
        try {
            RunTextModerationResponse response = moderationClient.runTextModeration(request);
            return Result.fromRunTextModerationResponse(response);
        } catch (ClientRequestException e) {
            throw new CommonException(CommonErrorCode.TEXT_MODERATION_ERROR,
                    ImmutableMap.<String, Object>builder()
                            .put("errorCode", e.getErrorCode())
                            .put("errorMsg", e.getErrorMsg())
                            .put("requestId", e.getRequestId())
                            .build()
            );
        }
    }
}
