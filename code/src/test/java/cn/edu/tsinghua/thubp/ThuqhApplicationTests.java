package cn.edu.tsinghua.thubp;

import static org.assertj.core.api.Assertions.assertThat;

import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ThuqhApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
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
