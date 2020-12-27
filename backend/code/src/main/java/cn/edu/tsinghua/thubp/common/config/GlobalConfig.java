package cn.edu.tsinghua.thubp.common.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URL;

@Getter
@Component
@PropertySource(value = {"classpath:config/config-template.properties", "classpath:config/config.properties"},
        ignoreResourceNotFound = true)
public class GlobalConfig {
    public static final URL DEFAULT_IMAGE_URL = getDefaultImageUrl();
    public static final URL DEFAULT_MATCH_LARGE_IMAGE_URL = getDefaultMatchLargeImageUrl();

    @SneakyThrows
    private static URL getDefaultImageUrl() {
        return new URL("http://thubp-static.iterator-traits.com/default.png");
    }

    @SneakyThrows
    private static URL getDefaultMatchLargeImageUrl() {
        return new URL("http://thubp-static.iterator-traits.com/match_default_large.png");
    }

    @Autowired
    private Environment env;

    @Value("${qiniu.enable}")
    private boolean QiNiuEnable;
    @Value("${qiniu.accesskey}")
    private String QiNiuAccessKey;
    @Value("${qiniu.privatekey}")
    private String QiNiuPrivateKey;
    @Value("${qiniu.bucketname}")
    private String QiNiuBucketName;
    @Value("${qiniu.host}")
    private String QiNiuHost;
    @Value("${qiniu.protocol}")
    private String QiNiuProtocol;
    @Value("${huawei.enable}")
    private boolean HuaweiEnable;
    @Value("${huawei.accesskey}")
    private String HuaweiAccessKey;
    @Value("${huawei.privatekey}")
    private String HuaweiPrivateKey;
    @Value("${huawei.projectid}")
    private String HuaweiProjectId;
    @Value("${huawei.endpoint}")
    private String HuaweiEndPoint;

    @Value("${thuauth.enable}")
    private boolean ThuAuthEnable;

    @Nullable
    public String getEnv(String k) {
        return env.getProperty(k);
    }
}
