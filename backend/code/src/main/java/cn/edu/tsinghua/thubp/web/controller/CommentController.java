package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.comment.service.CommentService;
import cn.edu.tsinghua.thubp.common.util.SwaggerTagUtil;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.CommentModifyRequest;
import cn.edu.tsinghua.thubp.web.request.CommentRequest;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.response.CommentResponse;
import cn.edu.tsinghua.thubp.web.response.MatchCreateResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(WebConstant.URL_PREFIX_API_V1)
public class CommentController {
    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    @ApiOperation(value = "评论赛事", tags = SwaggerTagUtil.COMMENT)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "matchId", value = "赛事 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/comment/match/{matchId}", method = RequestMethod.POST)
    public CommentResponse commentMatch(@PathVariable String matchId,
                                        @RequestBody @Valid CommentRequest commentRequest) {
        String commentId = commentService.commentMatch(currentUserService.getUserId(), matchId, commentRequest);
        return new CommentResponse(commentId);
    }

    @ApiOperation(value = "评论比赛", tags = SwaggerTagUtil.COMMENT)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "gameId", value = "比赛 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/comment/game/{gameId}", method = RequestMethod.POST)
    public CommentResponse commentGame(@PathVariable String gameId,
                                        @RequestBody @Valid CommentRequest commentRequest) {
        String commentId = commentService.commentGame(currentUserService.getUserId(), gameId, commentRequest);
        return new CommentResponse(commentId);
    }

    @ApiOperation(value = "修改评论", tags = SwaggerTagUtil.COMMENT)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "commentId", value = "评论 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/comment/{commentId}", method = RequestMethod.PATCH)
    public CommentResponse modifyComment(@PathVariable String commentId,
                                        @RequestBody @Valid CommentModifyRequest commentModifyRequest) {
        String ret = commentService.modifyComment(currentUserService.getUserId(), commentId, commentModifyRequest);
        return new CommentResponse(ret);
    }

    @ApiOperation(value = "删除评论", tags = SwaggerTagUtil.COMMENT)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "commentId", value = "评论 ID", required = true, dataTypeClass = String.class)
    )
    @ResponseBody
    @RequestMapping(value = "/comment/{commentId}", method = RequestMethod.DELETE)
    public CommentResponse deleteComment(@PathVariable String commentId) {
        String ret = commentService.deleteComment(currentUserService.getUserId(), commentId);
        return new CommentResponse(ret);
    }
}
