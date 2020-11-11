package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.match.entity.UnitToken;
import cn.edu.tsinghua.thubp.match.service.GameService;
import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.match.entity.RefereeToken;
import cn.edu.tsinghua.thubp.match.service.RoundService;
import cn.edu.tsinghua.thubp.match.service.UnitService;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.*;
import cn.edu.tsinghua.thubp.web.response.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class MatchController {

    private final CurrentUserService currentUserService;
    private final MatchService matchService;
    private final RoundService roundService;
    private final GameService gameService;
    private final UnitService unitService;

    @ApiOperation(value = "创建比赛", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ResponseBody
    @RequestMapping(value = "/match/create", method = RequestMethod.POST)
    public MatchCreateResponse create(@RequestBody @Valid MatchCreateRequest matchCreateRequest) {
        User user = currentUserService.getUser();
        String matchId = matchService.createMatch(user, matchCreateRequest);
        return new MatchCreateResponse(matchId);
    }

    @ApiOperation(value = "报名比赛（创建参赛单位）", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/register/{matchId}", method = RequestMethod.POST)
    public MatchRegisterResponse register(@PathVariable String matchId,
                                          @RequestBody @Valid MatchRegisterRequest matchRegisterRequest) {
        User user = currentUserService.getUser();
        String ret = unitService.registerIn(user, matchId, matchRegisterRequest);
        return new MatchRegisterResponse(ret);
    }

    @ApiOperation(value = "加入参赛单位", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/participate/{unitId}", method = RequestMethod.POST)
    public SimpleResponse participate(@PathVariable String unitId,
                                      @RequestBody @Valid UnitParticipateRequest unitParticipateRequest) {
        User user = currentUserService.getUser();
        unitService.participateIn(user, unitId, unitParticipateRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "签发参赛单位邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "unitId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-unit-token/{unitId}", method = RequestMethod.POST)
    public AssignUnitTokenResponse assignUnitToken(@PathVariable String unitId) {
        User user = currentUserService.getUser();
        UnitToken token = unitService.assignUnitToken(user.getUserId(), unitId);
        return AssignUnitTokenResponse.builder().
                token(token.getToken())
                .expirationTime(token.getExpirationTime().toEpochMilli())
                .build();
    }

    @ApiOperation(value = "签发裁判邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-referee-token/{matchId}", method = RequestMethod.POST)
    public AssignRefereeTokenResponse assignRefereeToken(@PathVariable String matchId) {
        User user = currentUserService.getUser();
        RefereeToken token = matchService.assignRefereeToken(user.getUserId(), matchId);
        return AssignRefereeTokenResponse.builder().
                token(token.getToken())
                .expirationTime(token.getExpirationTime().toEpochMilli())
                .build();
    }

    @ApiOperation(value = "使用裁判邀请码成为裁判", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/become-referee/{matchId}", method = RequestMethod.POST)
    public SimpleResponse becomeRefereeByToken(@PathVariable String matchId,
                                               @RequestBody @Valid BecomeRefereeRequest becomeRefereeRequest) {
        User user = currentUserService.getUser();
        matchService.becomeRefereeByToken(user.getUserId(), matchId, becomeRefereeRequest.getToken());
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "创建轮次", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round", method = RequestMethod.POST)
    public RoundCreateResponse createRound(@PathVariable String matchId,
                                           @RequestBody @Valid RoundCreateRequest roundCreateRequest) {
        User user = currentUserService.getUser();
        String roundId = roundService.createRound(user, matchId, roundCreateRequest);
        return new RoundCreateResponse(roundId);
    }

    @ApiOperation(value = "轮次中增加比赛", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}/game", method = RequestMethod.POST)
    public GameCreateResponse createGame(@PathVariable String matchId,
                                          @PathVariable String roundId,
                                          @RequestBody @Valid GameCreateRequest gameCreateRequest) {
        User user = currentUserService.getUser();
        String gameId = gameService.createGame(user, matchId, roundId, gameCreateRequest);
        return new GameCreateResponse(gameId);
    }

    @ApiOperation(value = "轮次中删除比赛", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}/game", method = RequestMethod.DELETE)
    public SimpleResponse createGame(@PathVariable String matchId,
                                         @PathVariable String roundId,
                                         @RequestBody @Valid GameDeleteRequest gameDeleteRequest) {
        User user = currentUserService.getUser();
        gameService.deleteGame(user, matchId, roundId, gameDeleteRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }
}
