package cn.edu.tsinghua.thubp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import cn.edu.tsinghua.thubp.common.exception.CommonExceptionHandler;
import cn.edu.tsinghua.thubp.common.exception.ErrorResponse;
import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.enums.RoundStatus;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.security.constant.SecurityConstant;
import cn.edu.tsinghua.thubp.security.dto.LoginRequest;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.controller.AdminController;
import cn.edu.tsinghua.thubp.web.request.*;
import cn.edu.tsinghua.thubp.web.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatchTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private AdminController adminController;
    
    private Map<String, String> emptyMap = new HashMap<String, String>();

    @Test
    @Order(0)
    void resetDatabase() {
        adminController._resetDatabase();
    }

    @Test
    @Order(1)
    void login() {
        // 登录
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

        String rootUri = restTemplate.getRootUri();
        testUtil.user2Template.setUriTemplateHandler(new DefaultUriBuilderFactory(rootUri));
        testUtil.user3Template.setUriTemplateHandler(new DefaultUriBuilderFactory(rootUri));

        // 注册一个假用户
        UserRegisterRequest userRegisterRequest2 = new UserRegisterRequest(
                "fake2",
                "123456",
                "12345678901",
                "a@b.c",
                "description",
                "2018000001"
        );
        testUtil.user2Id = userService.saveLegacy(userRegisterRequest2);
        LoginRequest loginRequest2 = new LoginRequest(
                "2018000001",
                "123456",
                true
        );
        ResponseEntity<String> response2 = this.restTemplate.postForEntity("/api/v1/auth/login", loginRequest2, String.class);
        final String auth2 = response2.getHeaders().getFirst(SecurityConstant.TOKEN_HEADER);
        testUtil.user2Template.getRestTemplate().setInterceptors(
                ImmutableList.of((request, body, execution) -> {
                    request.getHeaders()
                            .add(SecurityConstant.TOKEN_HEADER, auth2);
                    return execution.execute(request, body);
                }));
        assertThat(auth2).isNotNull();

        // 注册一个假用户
        UserRegisterRequest userRegisterRequest3 = new UserRegisterRequest(
                "fake3",
                "123456",
                "12345678901",
                "a@b.c",
                "description",
                "2018000002"
        );
        testUtil.user3Id = userService.saveLegacy(userRegisterRequest3);
        LoginRequest loginRequest3 = new LoginRequest(
                "2018000002",
                "123456",
                true
        );
        ResponseEntity<String> response3 = this.restTemplate.postForEntity("/api/v1/auth/login", loginRequest3, String.class);
        final String auth3 = response3.getHeaders().getFirst(SecurityConstant.TOKEN_HEADER);
        testUtil.user3Template.getRestTemplate().setInterceptors(
                ImmutableList.of((request, body, execution) -> {
                    request.getHeaders()
                            .add(SecurityConstant.TOKEN_HEADER, auth3);
                    return execution.execute(request, body);
                }));
        assertThat(auth3).isNotNull();
    }

    @Test
    @Order(2)
    void createMatch_open_1() {
        // 创建赛事_公开1
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
    void fetchMatchInfo_open() {
        // 获取赛事所有信息_公开
        assertThat(testUtil.saveResponse("获取赛事所有信息_公开",
                this.restTemplate.getForObject("/api/v1/match/{matchId}", MatchInfoResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(4)
    void createMatch_nonOpen_2() {
        // 创建赛事_非公开2
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
    void fetchMatchInfo_nonOpen() {
        // 获取赛事所有信息_非公开
        assertThat(testUtil.saveResponse("获取赛事所有信息_非公开",
                this.restTemplate.getForObject("/api/v1/match/{matchId}", MatchInfoResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(6)
    void modifyMatchInfo_open_1() {
        // 修改赛事信息_公开1
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
    @Order(7)
    void inviteMatchAndSendNotifications() {
        // 邀请用户加入赛事并发送通知
        MatchInviteRequest request = MatchInviteRequest.builder()
                .userIds(ImmutableList.of("1", "3", "2", "4"))
                .build();
        assertThat(this.restTemplate.postForObject("/api/v1/match/invite-match/{matchId}", request, InviteResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId()
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(8)
    void registerMatch_open() {
        // 报名赛事_公开
        MatchRegisterRequest request = MatchRegisterRequest.builder()
                .unitName("参赛单位1")
                .description("serious")
                .build();
        MatchRegisterResponse response = this.testUtil.user2Template.postForObject("/api/v1/match/register/{matchId}", request, MatchRegisterResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId()
        );
        testUtil.saveResponse("报名赛事_公开", response);
        assertThat(response.getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(9)
    void registerMatch_nonOpen() {
        // 报名赛事_非公开
        MatchRegisterRequest request = MatchRegisterRequest.builder()
                .token(
                        testUtil.<MatchInfoResponse>getResponse("获取赛事所有信息_非公开").getMatchToken().getToken()
                )
                .unitName("参赛单位2")
                .description("serious")
                .build();
        MatchRegisterResponse response = this.testUtil.user2Template.postForObject("/api/v1/match/register/{matchId}", request, MatchRegisterResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId()
        );
        assertThat(testUtil.saveResponse("报名赛事_非公开", response).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(10)
    void findMatch() throws JsonProcessingException {
        // 查找赛事
        ResponseEntity<String> response = this.restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {  findMatchById(matchId: \"" +
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId() +
                        "\") { " +
                        "status publicSignUp publicShowUp matchId name description preview minUnitMember " +
                        "maxUnitMember matchTypeId organizerUser {userId gender} participants {userId gender} " +
                        "referees {userId gender} rounds {roundId}} }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    @Test
    @Order(11)
    void findMatch_2() throws JsonProcessingException {
        // 查找赛事_2
        ResponseEntity<String> response = this.restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {  findMatches(matchIds: " +
                        "[\"" + testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId() + "\", " +
                        "\"" + testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId() + "\"]) " +
                        "{  matchId name description minUnitMember maxUnitMember matchTypeId organizerUser {userId gender} " +
                        "participants {userId gender} referees {userId gender} units {name creator { userId } }} }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    @Test
    @Order(12)
    void findMatchByType() throws JsonProcessingException {
        // 按类别查找赛事
        ResponseEntity<String> response = this.restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {  findMatchesByType(typeIds: [\"basketball\"]) {  page " +
                        "pageSize totalSize list { matchId name description matchTypeId organizerUser {userId gender} " +
                        "participants {userId gender} referees {userId gender} startTime targetGroup } } }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    @Test
    @Order(13)
    void findAllMatches() throws JsonProcessingException {
        // 查找所有赛事
        ResponseEntity<String> response = this.restTemplate.getForEntity("/api/v1/graphql?query={query}", String.class,
                ImmutableMap.of("query", "query {  findMatchesByType(typeIds: []) {  page pageSize totalSize " +
                        "list { matchId name active description matchTypeId startTime targetGroup organizerUser " +
                        "{userId gender} participants {userId gender} referees {userId gender} units {unitId} } } }"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String res = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(res);
        JsonNode data = root.get("data");
        assertThat(data).isNotNull();
        assertThat(data.isNull()).isFalse();
    }

    @Test
    @Order(14)
    void inviteReferee_tokenNotAssigned() {
        // 邀请用户成为裁判_未签发邀请码
        InviteRefereesRequest request = InviteRefereesRequest.builder()
                .userIds(ImmutableList.of("2", "3", "1", "4"))
                .build();
        assertThat(this.restTemplate.postForObject("/api/v1/match/invite-referees/{matchId}", request, SimpleResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId()
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(15)
    void assignRefereeToken_open() {
        // 签发裁判邀请码_公开
        assertThat(testUtil.saveResponse("签发裁判邀请码_公开",
                this.restTemplate.postForObject("/api/v1/match/assign-referee-token/{matchId}", emptyMap, AssignRefereeTokenResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(16)
    void inviteReferees() {
        // 邀请用户成为裁判
        InviteRefereesRequest request = InviteRefereesRequest.builder()
                .userIds(ImmutableList.of("2", "3", "1", "4"))
                .build();
        assertThat(this.restTemplate.postForObject("/api/v1/match/invite-referees/{matchId}", request, SimpleResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId()).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(17)
    void becomeReferee_open() {
        // 使用裁判邀请码成为裁判_公开
        BecomeRefereeRequest request = BecomeRefereeRequest.builder()
                .token(
                        testUtil.<AssignRefereeTokenResponse>getResponse("签发裁判邀请码_公开").getToken()
                )
                .build();
        // 你已经是
        SimpleResponse response = this.testUtil.user2Template.postForObject("/api/v1/match/become-referee/{matchId}", request, SimpleResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId());
        assertThat(response.getMessage().equals("ok")).isFalse();
    }

    @Test
    @Order(18)
    void deleteReferee() {
        // 删除裁判
        RefereeDeleteRequest request = RefereeDeleteRequest.builder()
                .referees(
                        ImmutableList.of(testUtil.user2Id)
                )
                .build();
        SimpleResponse response = this.restTemplate.postForObject("/api/v1/match/{matchId}/referee", request, SimpleResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId());
        assertThat(response.getMessage().equals("ok")).isFalse();
    }

    @Test
    @Order(19)
    void becomeReferee_open_copy() {
        // 使用裁判邀请码成为裁判_公开_copy
        BecomeRefereeRequest request = BecomeRefereeRequest.builder()
                .token(testUtil.<AssignRefereeTokenResponse>getResponse("签发裁判邀请码_公开").getToken())
                .build();
        AssignRefereeTokenResponse response = this.testUtil.user2Template.postForObject("/api/v1/match/become-referee/{matchId}",
                request, AssignRefereeTokenResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId());
        assertThat(response.getMessage().equals("ok")).isFalse();
    }

    @Test
    @Order(20)
    void fetchUnitInfo_nonOpen() {
        // 获取参赛单位所有信息_未公开
        assertThat(testUtil.saveResponse("获取参赛单位所有信息_未公开",
                this.testUtil.user2Template.getForObject("/api/v1/match/{matchId}/unit/{unitId}", UnitInfoResponse.class,
                        testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId(),
                        testUtil.<MatchRegisterResponse>getResponse("报名赛事_非公开").getUnitId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(21)
    void participateUnit_nonOpen() {
        // 加入参赛单位_未公开
        UnitParticipateRequest request = UnitParticipateRequest.builder()
                .token(testUtil.<UnitInfoResponse>getResponse("获取参赛单位所有信息_未公开").getUnitToken().getToken())
                .build();
        ErrorResponse response = this.testUtil.user3Template.postForObject("/api/v1/match/participate/{unitId}", request, ErrorResponse.class,
                testUtil.<MatchRegisterResponse>getResponse("报名赛事_非公开").getUnitId());
        assertThat(response.getCode() == MatchErrorCode.MATCH_ALREADY_PARTICIPATED.getCode()).isFalse();
    }

    @Test
    @Order(22)
    void assignUnitToken_nonOpen() {
        // 签发参赛单位邀请码_未公开
        assertThat(testUtil.saveResponse("签发参赛单位邀请码_未公开",
                this.testUtil.user2Template.postForObject("/api/v1/match/assign-unit-token/{unitId}", emptyMap, SimpleResponse.class,
                        testUtil.<MatchRegisterResponse>getResponse("报名赛事_非公开").getUnitId())
        ).getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(23)
    void participateUnit_nonOpen_2() {
        // 加入参赛单位_未公开_2
        UnitParticipateRequest request = UnitParticipateRequest.builder()
                .token(testUtil.<UnitInfoResponse>getResponse("获取参赛单位所有信息_未公开").getUnitToken().getToken())
                .build();
        SimpleResponse response = this.testUtil.user3Template.postForObject("/api/v1/match/participate/{unitId}", request, SimpleResponse.class,
                testUtil.<MatchRegisterResponse>getResponse("报名赛事_非公开").getUnitId());
        assertThat(response.getMessage().equals("ok")).isFalse();
    }

    @Test
    @Order(24)
    void generateGamesByStrategy() {
        // 按策略生成赛事安排
        GameGenerateRequest request = GameGenerateRequest.builder()
                .strategy("SINGLE_ROUND")
                .units(ImmutableList.of(testUtil.<MatchRegisterResponse>getResponse("报名赛事_非公开").getUnitId()))
                .build();
        GameGenerateResponse response = this.restTemplate.postForObject("/api/v1/match/{matchId}/generate-games", request, GameGenerateResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_非公开2").getMatchId()
        );
        assertThat(response.getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(25)
    void createRound_open_custom() {
        // 创建轮次_公开_自定义
        String unit0 = testUtil.<MatchRegisterResponse>getResponse("报名赛事_公开").getUnitId();
        RoundCreateRequest request = RoundCreateRequest.builder()
                .description("自定义")
                .name("测试轮次")
                .units(ImmutableList.of(unit0))
                .games(ImmutableList.of(
                        GameCreateRequest.builder()
                                .unit0(unit0)
                                .unit1(null)
                                .startTime(Instant.parse("2020-12-14T12:46:05.368Z"))
                                .location("紫草")
                                .build()
                ))
                .build();
        RoundCreateResponse response = this.restTemplate.postForObject("/api/v1/match/{matchId}/round", request, RoundCreateResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId()
        );
        testUtil.saveResponse("创建轮次_公开_自定义", response);
        assertThat(response.getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(26)
    void addGameToRound_open() {
        // 轮次中增加比赛_公开
        String unit0 = testUtil.<MatchRegisterResponse>getResponse("报名赛事_公开").getUnitId();
        GameCreateRequest request = GameCreateRequest.builder()
                .unit0(unit0)
                .build();
        GameCreateResponse response = this.restTemplate.postForObject("/api/v1/match/{matchId}/round/{roundId}/game", request, GameCreateResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId(),
                testUtil.<RoundCreateResponse>getResponse("创建轮次_公开_自定义").getRoundId()
        );
        testUtil.saveResponse("轮次中增加比赛_公开", response);
        assertThat(response.getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(27)
    void modifyRound() {
        // 修改轮次基本信息
        RoundModifyRequest request = RoundModifyRequest.builder()
                .name("test-new_name")
                .description("emm好像没什么描述的")
                .tag("第一轮小组赛")
                .status(RoundStatus.NOT_START)
                .build();
        SimpleResponse response = this.restTemplate.postForObject("/api/v1/match/{matchId}/round/{roundId}", request, SimpleResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId(),
                testUtil.<RoundCreateResponse>getResponse("创建轮次_公开_自定义").getRoundId()
        );
        assertThat(response.getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(28)
    void createGame_open_2() {
        // 轮次中增加比赛_公开_2
        String unit0 = testUtil.<MatchRegisterResponse>getResponse("报名赛事_公开").getUnitId();
        GameCreateRequest request = GameCreateRequest.builder()
                .unit0(unit0)
                .build();
        GameCreateResponse response = this.restTemplate.postForObject("/api/v1/match/{matchId}/round/{roundId}/game", request, GameCreateResponse.class,
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId(),
                testUtil.<RoundCreateResponse>getResponse("创建轮次_公开_自定义").getRoundId()
        );
        testUtil.saveResponse("轮次中增加比赛_公开2", response);
        assertThat(response.getMessage().equals("ok")).isTrue();
    }

    @Test
    @Order(29)
    void deleteGame() {
        // 轮次中删除比赛
        GameDeleteRequest request = GameDeleteRequest.builder()
                .games(ImmutableList.of(testUtil.<GameCreateResponse>getResponse("轮次中增加比赛_公开2").getGameId()))
                .build();
        this.restTemplate.delete("/api/v1/match/{matchId}/round/{roundId}/game",
                testUtil.<MatchCreateResponse>getResponse("创建赛事_公开1").getMatchId(),
                testUtil.<RoundCreateResponse>getResponse("创建轮次_公开_自定义").getRoundId()
        );
    }

    @Test
    @Order(30)
    void 修改比赛信息() {

    }

    @Test
    @Order(31)
    void 获取轮次信息() {

    }

    @Test
    @Order(32)
    void 修改比赛信息_2() {

    }

    @Test
    @Order(33)
    void 修改比赛信息_失败() {

    }

    @Test
    @Order(34)
    void 获取比赛信息() {

    }

    @Test
    @Order(35)
    void 删除轮次() {

    }

    @Test
    @Order(36)
    void 解散参赛单位() {

    }

}
