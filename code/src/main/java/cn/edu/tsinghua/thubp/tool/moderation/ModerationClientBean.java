package cn.edu.tsinghua.thubp.tool.moderation;

import cn.edu.tsinghua.thubp.common.config.GlobalConfig;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.http.HttpConfig;
import com.huaweicloud.sdk.moderation.v1.ModerationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModerationClientBean {
    @Autowired
    private GlobalConfig globalConfig;
    @Bean
    public ModerationClient moderationClient() {
        HttpConfig config = HttpConfig.getDefaultHttpConfig();
        BasicCredentials credentials = new BasicCredentials()
                .withAk(globalConfig.getHuaweiAccessKey())
                .withSk(globalConfig.getHuaweiPrivateKey())
                .withProjectId(globalConfig.getHuaweiProjectId());
        return ModerationClient.newBuilder()
                .withHttpConfig(config)
                .withCredential(credentials)
                .withEndpoint(globalConfig.getHuaweiEndPoint())
                .build();
    }
}
