package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.web.controller.AdminController;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
import cn.edu.tsinghua.thubp.web.response.UserInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
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
class UserTest {

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
    void 重置数据库_copy() {
        assertThat(this.restTemplate.getForObject("/api/v1/admin/reset", String.class)
                .contains("ok")).isTrue();
    }

    @Test
    @Order(3)
    void 个人信息() {
        UserInfoResponse response = restTemplate.getForObject("/api/v1/user/info", UserInfoResponse.class);
        assertThat(response.getMessage()).isEqualTo("ok");
    }

    @Test
    @Order(4)
    @SneakyThrows
    void 查找用户_root1() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query { findUserById(userIds: [\"1\"]) { gender mobile createdAt organizedMatches {matchId name} organizedMatchSize } }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    @Test
    @Order(5)
    @SneakyThrows
    void 查找用户_root2() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {  findUserByFuzzy(username: \"oo\", pageSize: 2) { page pageSize totalSize list {  gender  username mobile  } } }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    @Test
    @Order(6)
    @SneakyThrows
    void 修改个人信息() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setGender(Gender.FEMALE);
        request.setMobile("99999999999");
        request.setDescription("tired");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/user/info", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(7)
    @SneakyThrows
    void 修改个人信息_全空() {
        UserUpdateRequest request = new UserUpdateRequest();
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/user/info", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(8)
    @SneakyThrows
    void 获得所有的用户_管理员() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/admin/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
