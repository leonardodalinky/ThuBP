package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.match.entity.*;
import cn.edu.tsinghua.thubp.match.service.GameService;
import cn.edu.tsinghua.thubp.match.service.MatchService;
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
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class MatchController {

    private final CurrentUserService currentUserService;
    private final MatchService matchService;
    private final RoundService roundService;
    private final GameService gameService;
    private final UnitService unitService;

    @ApiOperation(value = "创建赛事", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ResponseBody
    @RequestMapping(value = "/match", method = RequestMethod.POST)
    public MatchCreateResponse createMatch(@RequestBody @Valid MatchCreateRequest matchCreateRequest) {
        User user = currentUserService.getUser();
        String matchId = matchService.createMatch(user, matchCreateRequest);
        return new MatchCreateResponse(matchId);
    }

    @ApiOperation(value = "获取赛事所有信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}", method = RequestMethod.GET)
    public MatchInfoResponse infoMatch(@PathVariable String matchId) {
        User user = currentUserService.getUser();
        Match match = matchService.infoMatch(user, matchId);
        return new MatchInfoResponse(match);
    }

    @ApiOperation(value = "修改赛事信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}", method = RequestMethod.POST)
    public SimpleResponse modifyMatch(@PathVariable String matchId,
                                      @RequestBody @Valid MatchModifyRequest matchModifyRequest)
            throws MalformedURLException {
        User user = currentUserService.getUser();
        matchService.modifyMatch(user, matchId, matchModifyRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "签发赛事邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-match-token/{matchId}", method = RequestMethod.POST)
    public AssignMatchTokenResponse assignMatchToken(@PathVariable String matchId) {
        User user = currentUserService.getUser();
        MatchToken token = matchService.assignMatchToken(user.getUserId(), matchId);
        return AssignMatchTokenResponse.builder().
                token(token.getToken())
                .expirationTime(token.getExpirationTime())
                .build();
    }

    @ApiOperation(value = "报名赛事（创建参赛单位）", tags = SwaggerTagUtil.MATCH_MANAGE)
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

    @ApiOperation(value = "修改参赛单位", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/unit/{unitId}", method = RequestMethod.POST)
    public SimpleResponse modifyUnit(@PathVariable String matchId,
                                     @PathVariable String unitId,
                                     @RequestBody @Valid UnitModifyRequest unitModifyRequest) {
        User user = currentUserService.getUser();
        unitService.modifyUnit(user, matchId, unitId, unitModifyRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "获取参赛单位所有信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/unit/{unitId}", method = RequestMethod.GET)
    public UnitInfoResponse infoUnit(@PathVariable String matchId, @PathVariable String unitId) {
        User user = currentUserService.getUser();
        Unit unit = unitService.infoUnit(user, matchId, unitId);
        return new UnitInfoResponse(unit);
    }

    @ApiOperation(value = "签发参赛单位邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-unit-token/{unitId}", method = RequestMethod.POST)
    public AssignUnitTokenResponse assignUnitToken(@PathVariable String unitId) {
        User user = currentUserService.getUser();
        UnitToken token = unitService.assignUnitToken(user.getUserId(), unitId);
        return AssignUnitTokenResponse.builder().
                token(token.getToken())
                .expirationTime(token.getExpirationTime())
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
                .expirationTime(token.getExpirationTime())
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

    @ApiOperation(value = "删除轮次", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}", method = RequestMethod.DELETE)
    public SimpleResponse deleteRound(@PathVariable String matchId, @PathVariable String roundId) {
        User user = currentUserService.getUser();
        roundService.deleteRound(user, matchId, roundId);
        return new SimpleResponse(SimpleResponse.OK);
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

    @ApiOperation(value = "修改比赛信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}/game/{gameId}", method = RequestMethod.POST)
    public SimpleResponse modifyGame(@PathVariable String matchId,
                                         @PathVariable String roundId,
                                         @PathVariable String gameId,
                                         @RequestBody @Valid GameModifyRequest gameModifyRequest) {
        User user = currentUserService.getUser();
        gameService.modifyGame(user, matchId, roundId, gameId, gameModifyRequest);
        return new SimpleResponse(SimpleResponse.OK);
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
