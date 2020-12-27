package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.web.controller.AdminController;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.response.MatchCreateResponse;
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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BulletinTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestUtil testUtil;
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
    void 创建赛事_公开1() {
        /*
          {
            "name": "测试赛事1",
            "description": "测试赛事描述1",
            "matchTypeId": "tennis",
            "targetGroup": "软院1",
            "startTime": "2020-11-26T14:24:39.840Z",
            "preview": "MATCH_PREVIEW_1_123.jpg",
            "minUnitMember": 1,
            "maxUnitMember": 6,
            "publicSignUp": true,
            "publicShowUp": true
          }
         */
        MatchCreateRequest request = MatchCreateRequest.builder()
                .name("测试赛事1")
                .description("测试赛事描述1")
                .matchTypeId("tennis")
                .targetGroup("软院1")
                .startTime(Instant.parse("2020-11-26T14:24:39.840Z"))
                .preview("MATCH_PREVIEW_1_123.jpg")
                .minUnitMember(1)
                .maxUnitMember(6)
                .publicSignUp(true)
                .publicShowUp(true)
                .build();
        assertThat(testUtil.saveResponse("创建赛事_公开1",
                this.restTemplate.postForObject("/api/v1/match", request, MatchCreateResponse.class)
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(3)
    @SneakyThrows
    void 查询公告版() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {  getBulletin { preview previewLarge matchId } }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }
}
