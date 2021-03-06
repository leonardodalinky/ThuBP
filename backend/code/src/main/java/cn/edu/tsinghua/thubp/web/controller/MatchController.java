package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.bulletin.service.BulletinService;
import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.match.entity.*;
import cn.edu.tsinghua.thubp.match.misc.GameArrangement;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class MatchController {
    @Value("${spring.profiles.active}")
    private String profile;

    private final CurrentUserService currentUserService;
    private final MatchService matchService;
    private final RoundService roundService;
    private final GameService gameService;
    private final UnitService unitService;
    private final BulletinService bulletinService;

    @ApiOperation(value = "创建赛事", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ResponseBody
    @RequestMapping(value = "/match", method = RequestMethod.POST)
    public MatchCreateResponse createMatch(@RequestBody @Valid MatchCreateRequest matchCreateRequest) throws MalformedURLException {
        User user = currentUserService.getUser();
        String matchId = matchService.createMatch(user, matchCreateRequest);
        if (profile.equals("dev")) {
            bulletinService.update();
        }
        return new MatchCreateResponse(matchId);
    }

    @ApiOperation(value = "获取赛事所有信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}", method = RequestMethod.GET)
    public MatchInfoResponse infoMatch(@PathVariable String matchId) {
        Match match = matchService.infoMatch(currentUserService.getUserId(), matchId);
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
        matchService.modifyMatch(currentUserService.getUserId(), matchId, matchModifyRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "签发赛事邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-match-token/{matchId}", method = RequestMethod.POST)
    public AssignMatchTokenResponse assignMatchToken(@PathVariable String matchId) {
        MatchToken token = matchService.assignMatchToken(currentUserService.getUserId(), matchId);
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
        unitService.modifyUnit(currentUserService.getUserId(), matchId, unitId, unitModifyRequest);
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
        Unit unit = unitService.infoUnit(currentUserService.getUserId(), matchId, unitId);
        return new UnitInfoResponse(unit);
    }

    @ApiOperation(value = "删除参赛单位中成员", tags = SwaggerTagUtil.MATCH_MANAGE, notes = "小组队长限定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/unit/{unitId}/member", method = RequestMethod.DELETE)
    public SimpleResponse deleteUnitMember(@PathVariable String matchId,
                                           @PathVariable String unitId,
                                           @RequestBody @Valid UnitDeleteMemberRequest unitDeleteMemberRequest) {
        unitService.deleteUnitMembers(currentUserService.getUserId(), matchId, unitId,
                unitDeleteMemberRequest.getMembers(), true);
        return new SimpleResponse();
    }

    @ApiOperation(value = "解散参赛单位", tags = SwaggerTagUtil.MATCH_MANAGE, notes = "小组队长和组织者限定，须在比赛开始前")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/unit/{unitId}", method = RequestMethod.DELETE)
    public SimpleResponse deleteUnit(@PathVariable String matchId,
                                     @PathVariable String unitId) {
        unitService.deleteUnit(currentUserService.getUserId(), matchId, unitId, true);
        return new SimpleResponse();
    }

    @ApiOperation(value = "签发参赛单位邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-unit-token/{unitId}", method = RequestMethod.POST)
    public AssignUnitTokenResponse assignUnitToken(@PathVariable String unitId) {
        UnitToken token = unitService.assignUnitToken(currentUserService.getUserId(), unitId);
        return AssignUnitTokenResponse.builder().
                token(token.getToken())
                .expirationTime(token.getExpirationTime())
                .build();
    }

    @ApiOperation(value = "邀请人员加入参赛单位", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "unitId", value = "参赛单位 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-unit-token/{unitId}", method = RequestMethod.PUT)
    public SimpleResponse invite(@PathVariable String unitId, @RequestBody UnitInviteRequest unitInviteRequest) {
        unitService.inviteMembers(currentUserService.getUser(), unitId, unitInviteRequest.getUserIds());
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "邀请参加比赛", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/invite-match/{matchId}", method = RequestMethod.POST)
    public InviteResponse inviteMatch(@PathVariable String matchId,
                                      @RequestBody @Valid MatchInviteRequest matchInviteRequest) {
        User user = currentUserService.getUser();
        List<String> userIds = matchService.sendMatchInvitations(user, matchInviteRequest.getUserIds(), matchId);
        return new InviteResponse(userIds);
    }

    @ApiOperation(value = "邀请成为裁判", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/invite-referees/{matchId}", method = RequestMethod.POST)
    public InviteResponse inviteReferees(@PathVariable String matchId,
                                         @RequestBody @Valid InviteRefereesRequest inviteRefereesRequest) {
        User user = currentUserService.getUser();
        List<String> userIds = matchService.sendRefereeInvitations(user, inviteRefereesRequest.getUserIds(), matchId);
        return new InviteResponse(userIds);
    }

    @ApiOperation(value = "签发裁判邀请码", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/assign-referee-token/{matchId}", method = RequestMethod.POST)
    public AssignRefereeTokenResponse assignRefereeToken(@PathVariable String matchId) {
        RefereeToken token = matchService.assignRefereeToken(currentUserService.getUserId(), matchId);
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
        matchService.becomeRefereeByToken(currentUserService.getUserId(), matchId, becomeRefereeRequest.getToken());
        return new SimpleResponse(SimpleResponse.OK);
    }

    @ApiOperation(value = "删除裁判", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/referee", method = RequestMethod.DELETE)
    public SimpleResponse deleteReferee(@PathVariable String matchId,
                                        @RequestBody @Valid RefereeDeleteRequest refereeDeleteRequest) {
        matchService.dropReferees(currentUserService.getUserId(), matchId, refereeDeleteRequest);
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
        String roundId = roundService.createRound(currentUserService.getUserId(), matchId, roundCreateRequest);
        return new RoundCreateResponse(roundId);
    }

    @ApiOperation(value = "修改轮次基本信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}", method = RequestMethod.POST)
    public SimpleResponse modifyRound(@PathVariable String matchId, @PathVariable String roundId,
                                      @RequestBody @Valid RoundModifyRequest roundModifyRequest) {
        roundService.modifyRound(currentUserService.getUserId(), matchId, roundId, roundModifyRequest);
        return new SimpleResponse();
    }



    @ApiOperation(value = "自动生成比赛", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/generate-games", method = RequestMethod.POST)
    public GameGenerateResponse generateGames(@PathVariable String matchId,
                                              @RequestBody @Valid GameGenerateRequest gameGenerateRequest) {
        List<GameArrangement> gameList = roundService.generateGames(
                currentUserService.getUserId(), matchId, gameGenerateRequest);
        return new GameGenerateResponse(gameList);
    }

    @ApiOperation(value = "删除轮次", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}", method = RequestMethod.DELETE)
    public SimpleResponse deleteRound(@PathVariable String matchId, @PathVariable String roundId) {
        roundService.deleteRound(currentUserService.getUserId(), matchId, roundId);
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
        String gameId = gameService.createGame(currentUserService.getUserId(), matchId, roundId, gameCreateRequest);
        return new GameCreateResponse(gameId);
    }

    @ApiOperation(value = "修改比赛信息", tags = SwaggerTagUtil.MATCH_MANAGE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roundId", value = "轮次 ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "gameId", value = "比赛 ID", required = true, dataTypeClass = String.class)
    })
    @ResponseBody
    @RequestMapping(value = "/match/{matchId}/round/{roundId}/game/{gameId}", method = RequestMethod.POST)
    public SimpleResponse modifyGame(@PathVariable String matchId,
                                         @PathVariable String roundId,
                                         @PathVariable String gameId,
                                         @RequestBody @Valid GameModifyRequest gameModifyRequest) {
        gameService.modifyGame(currentUserService.getUserId(), matchId, roundId, gameId, gameModifyRequest);
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
        gameService.deleteGame(currentUserService.getUserId(), matchId, roundId, gameDeleteRequest);
        return new SimpleResponse(SimpleResponse.OK);
    }
}
