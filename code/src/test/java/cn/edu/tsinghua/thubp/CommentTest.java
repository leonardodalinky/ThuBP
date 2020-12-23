package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.web.controller.AdminController;
import cn.edu.tsinghua.thubp.web.request.CommentModifyRequest;
import cn.edu.tsinghua.thubp.web.request.CommentRequest;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.response.CommentResponse;
import cn.edu.tsinghua.thubp.web.response.MatchCreateResponse;
import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
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
public class CommentTest {
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

    /**
     * 登录
     */
    @Test
    @Order(1)
    void login() {
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

    /**
     * 创建赛事_公开1
     */
    @Test
    @Order(2)
    void createMatch() {
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

    /**
     * 评论赛事
     */
    @Test
    @Order(3)
    void commentMatch() {
        CommentRequest request = new CommentRequest();
        request.setContent("114514");
        MatchCreateResponse matchResponse = testUtil.getResponse("创建赛事_公开1");
        CommentResponse response = restTemplate.postForObject("/api/v1/comment/match/{matchId}",
                request,
                CommentResponse.class,
                ImmutableList.of("matchId", matchResponse.getMatchId())
        );
        testUtil.saveResponse("评论赛事", response);
        assertThat(response).isNotNull().hasFieldOrPropertyWithValue("message", SimpleResponse.OK);
    }

    /**
     * 修改评论
     */
    @Test
    @Order(4)
    void modifyComment() {
        CommentModifyRequest request = new CommentModifyRequest();
        request.setContent("1919810无敌");
        CommentResponse commentResponse = testUtil.getResponse("评论赛事");
        CommentResponse response = restTemplate.patchForObject("/api/v1/comment/{commentId}",
                request,
                CommentResponse.class,
                ImmutableMap.of("commentId", commentResponse.getCommentId())
        );
        assertThat(response).isNotNull().hasFieldOrPropertyWithValue("message", SimpleResponse.OK);
    }

    /**
     * 查看赛事的评论
     */
    @Test
    @Order(5)
    @SneakyThrows
    void getMatchComment() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {   findMatchById(matchId: \"1\") {comments(page: 0, pageSize: 10) {  commentId issuer { userId } content createdAt }   } }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    /**
     * 修改评论
     */
    @Test
    @Order(6)
    void deleteComment() {
        CommentResponse commentResponse = testUtil.getResponse("评论赛事");
        restTemplate.delete("/api/v1/comment/{commentId}",
                ImmutableMap.of("commentId", commentResponse.getCommentId())
        );
    }
}
