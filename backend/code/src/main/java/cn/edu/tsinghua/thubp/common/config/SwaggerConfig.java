package cn.edu.tsinghua.thubp.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static springfox.documentation.builders.PathSelectors.regex;

//@Profile("dev")
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {
    @Value("${server.port}")
    private Long port;
    @Value("${springfox.documentation.enabled}")
    private boolean enable;

    @Bean
    public Docket api() throws MalformedURLException {
        if (!enable)
            return new Docket(DocumentationType.SWAGGER_2);
        log.info("生成 Swagger2 文档");
        URL url = new URL("http", "0.0.0.0", port.intValue(), "/swagger-ui/");
        URL localUrl = new URL("http", "localhost", port.intValue(), "/swagger-ui/");
        log.info("开放 Swagger 文档: " + url.toString());
        log.info("本地可访问 Swagger: " + localUrl.toString());
        ApiInfo apiInfo = new ApiInfo(
                "清球汇 API 文档",
                "通过 Swagger2 产生的清球汇文档",
                "1.0.0",
                null,
                new Contact("清球汇后端小组", "https://github.com/leonardodalinky/ThuBP-backend", null),
                "MIT",
                "https://github.com/leonardodalinky/ThuBP-backend/blob/main/LICENSE",
                new ArrayList<>()
                );
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
                .paths(regex("/api/v1/.*"))
                .build()
                .apiInfo(apiInfo);
    }
}
