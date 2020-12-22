package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.web.controller.AdminController;
import cn.edu.tsinghua.thubp.web.enums.UploadType;
import cn.edu.tsinghua.thubp.web.request.UploadRequest;
import cn.edu.tsinghua.thubp.web.response.UploadResponse;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UploadTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AdminController adminController;

    @Test
    @Order(0)
    void resetDatabase() {
        adminController._resetDatabase();
    }

    @Test
    @Order(1)
    void 登录() {
        LoginRequest loginRequest = new LoginRequest(
                "2018000000",
                "root",
                true
        );
        ResponseEntity<String> response = this.restTemplate.postForEntity("/api/v1/auth/login", loginRequest, String.class);
        final String auth = response.getHeaders().getFirst(SecurityConstant.TOKEN_HEADER);
        restTemplate.getRestTemplate().setInterceptors(
                ImmutableList.of((request, body, execution) -> {
                    request.getHeaders()
                            .add(SecurityConstant.TOKEN_HEADER, auth);
                    return execution.execute(request, body);
                }));
        assertThat(auth).isNotNull();
    }

    @Test
    @Order(2)
    void 请求上传凭证_nosuffix() {
        UploadRequest request = new UploadRequest();
        request.setUploadType(UploadType.AVATAR);
        UploadResponse response = restTemplate.postForObject("/api/v1/upload", request, UploadResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.getKey()).contains(UploadType.AVATAR.getName());
        assertThat(response.getUploadToken()).isNotEmpty();
    }

    @Test
    @Order(3)
    void 请求上传凭证_suffix() {
        UploadRequest request = new UploadRequest();
        request.setUploadType(UploadType.AVATAR);
        request.setSuffix("png");
        UploadResponse response = restTemplate.postForObject("/api/v1/upload", request, UploadResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.getKey()).contains(UploadType.AVATAR.getName()).contains("png");
        assertThat(response.getUploadToken()).isNotEmpty();
    }
}
