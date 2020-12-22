package cn.edu.tsinghua.thubp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.request.MatchModifyRequest;
import cn.edu.tsinghua.thubp.web.request.MatchRegisterRequest;
import cn.edu.tsinghua.thubp.web.response.LoginResponse;
import cn.edu.tsinghua.thubp.web.response.MatchCreateResponse;
import cn.edu.tsinghua.thubp.web.response.MatchInfoResponse;
import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
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

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatchTestSuite {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestUtil testUtil;

    @Test
    @Order(0)
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
    @Order(1)
    void 重置数据库_copy() {
        assertThat(this.restTemplate.getForObject("/api/v1/admin/reset", SimpleResponse.class).getMessage().equals("ok")).isTrue();
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
    void 获取赛事所有信息_公开() {
        assertThat(testUtil.saveResponse("获取赛事所有信息_公开",
                this.restTemplate.getForObject("/api/v1/match/{matchId}", MatchInfoResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(4)
    void 创建赛事_非公开2() {
        MatchCreateRequest request = MatchCreateRequest.builder()
                .name("测试赛事2")
                .description("测试赛事描述2")
                .matchTypeId("basketball")
                .targetGroup("软院2")
                .startTime(Instant.parse("2020-11-26T14:24:39.840Z"))
                .minUnitMember(2)
                .maxUnitMember(2)
                .publicSignUp(false)
                .publicShowUp(false)
                .build();
        assertThat(testUtil.saveResponse("创建赛事_非公开2", this.restTemplate.postForObject("/api/v1/match", request, MatchCreateResponse.class))
                .getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(5)
    void 获取赛事所有信息_非公开() {
        assertThat(testUtil.saveResponse("获取赛事所有信息_非公开",
                this.restTemplate.getForObject("/api/v1/match/{matchId}", MatchInfoResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(6)
    void 修改赛事信息_公开1() {
        MatchModifyRequest request = MatchModifyRequest.builder()
                .description("modify")
                .name("modify_public")
                .targetGroup("thuer")
                .build();
        assertThat(this.restTemplate.postForObject("/api/v1/match/{matchId}", request, SimpleResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId()
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(6)
    void 报名赛事_公开() {
        MatchRegisterRequest request = MatchRegisterRequest.builder()
                .unitName("参赛单位1")
                .description("serious")
                .build();
        assertThat(this.restTemplate.postForObject("/api/v1/match/register/{matchId}", request, SimpleResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId()
        ).getMessage().equals("ok")).isTrue();
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
