package cn.edu.tsinghua.thubp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.response.LoginResponse;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatchTestSuite {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(0)
    void 登录() {
        LoginRequest loginRequest = new LoginRequest(
                "2018000000",
                "root",
                true
        );
        ResponseEntity<LoginResponse> response = this.restTemplate.postForEntity("/api/v1/auth/login", loginRequest, LoginResponse.class);
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
    @Order(1)
    void 重置数据库_copy() {
        assertThat(this.restTemplate.postForObject("/api/v1/admin/reset", new Object(), String.class)
                .contains("ok")).isTrue();
    }

    @Test
    @Order(2)
    void 创建赛事_公开1() {
    }

    @Test
    @Order(3)
    void 获取赛事所有信息_公开() {
    }

    @Test
    @Order(4)
    void 创建赛事_非公开2() {

    }

    @Test
    @Order(5)
    void 获取赛事所有信息_非公开() {
    }

    @Test
    @Order(6)
    void 修改赛事信息_公开1() {
    }

//    @Test
//    void hello() {
//        assertThat("hello word").isNotEqualTo("hello world");
//    }
//
//    @Test
//    void login() throws Exception {
//        LoginRequest loginRequest = new LoginRequest("zhy", "123456", true);
//        assertThat(this.restTemplate.postForObject("/api/v1/auth/login", loginRequest, String.class)
//                .contains("ok")).isTrue();
//    }

}
