package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UnitResolver implements GraphQLResolver<Unit> {
    private final UserService userService;
    private final MatchService matchService;
    private final CurrentUserService currentUserService;

    public User creator(Unit unit) {
        return userService.findByUserId(unit.getCreatorId());
    }

    public List<User> members(Unit unit) {
        return userService.findByUserIdIn(unit.getMembers());
    }

    public Match match(Unit unit) {
        return matchService.findByMatchId(unit.getMatchId(), true, currentUserService.getUserId());
    }
}
