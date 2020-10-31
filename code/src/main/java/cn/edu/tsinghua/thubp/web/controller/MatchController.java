package cn.edu.tsinghua.thubp.web.controller;

import cn.edu.tsinghua.thubp.match.MatchService;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.constant.WebConstant;
import cn.edu.tsinghua.thubp.web.request.MatchCreateRequest;
import cn.edu.tsinghua.thubp.web.response.MatchCreateResponse;
import cn.edu.tsinghua.thubp.web.response.SimpleResponse;
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

    @ResponseBody
    @RequestMapping(value = "/match/create", method = RequestMethod.POST)
    public MatchCreateResponse create(@RequestBody @Valid MatchCreateRequest matchCreateRequest) {
        User user = currentUserService.getUser();
        String matchId = matchService.save(user, matchCreateRequest);
        return new MatchCreateResponse(matchId);
    }

    @ResponseBody
    @RequestMapping(value = "/match/participate/{id}", method = RequestMethod.POST)
    public SimpleResponse participate(@PathVariable String id) {
        User user = currentUserService.getUser();
        matchService.participateIn(user, id);
        return new SimpleResponse(SimpleResponse.OK);
    }
}
