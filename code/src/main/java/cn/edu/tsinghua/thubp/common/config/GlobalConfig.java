package cn.edu.tsinghua.thubp.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(value = "classpath:config/config.properties")
public class GlobalConfig {
    @Autowired
    private Environment env;

    @Value("${qiniu.accesskey}")
    private String QiNiuAccessKey;
    @Value("${qiniu.privatekey}")
    private String QiNiuPrivateKey;
    @Value("${qiniu.bucketname}")
    private String QiNiuBucketName;

    @Nullable
    public String getEnv(String k) {
        return env.getProperty(k);
    }
}
