package cn.edu.tsinghua.thubp.web.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebConstant {
    public static final String URL_PREFIX_API_V1 = "/api/v1";
    /**
     * 七牛云公私密钥
     */
    public static final String QINIUYUN_ACCESS_KEY = "UAsu8ryKDFnV1v89WaFFLuo1JsZZDemlgjodM-5O";
    public static final String QINIUYUN_SECRET_KEY = "f6hpiIDkJWhRETzEPPHdwjVjPFOxjWJzYaOlLgg_";
    public static final String QINIUYUN_BUCKET_NAME = "thubp";
}
