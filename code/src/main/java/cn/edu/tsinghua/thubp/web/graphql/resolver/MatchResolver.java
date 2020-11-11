package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.plugin.MatchType;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchResolver implements GraphQLResolver<Match> {
    private final UserService userService;

    public User organizerUser(Match match) {
        return userService.findByUserId(match.getOrganizerUserId());
    }

    public List<User> participants(Match match) {
        return userService.findByUserIdIn(match.getParticipants());
    }

    public List<User> referees(Match match) {
        return userService.findByUserIdIn(match.getReferees());
    }
}
