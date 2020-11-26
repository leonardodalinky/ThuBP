package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.repository.MatchRepository;
import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserResolver implements GraphQLResolver<User> {
    private final MatchRepository matchRepository;
    private final MatchService matchService;

    public String avatar(User user) {
        return (user.getAvatar() == null)? null : user.getAvatar().toString();
    }

    public List<Match> organizedMatches(User user, Integer page, Integer pageSize) {
        return matchService.findMatchesByMatchIds(user.getOrganizedMatches(), PageRequest.of(page, pageSize), false, null);
    }

    public List<Match> participatedMatches(User user, Integer page, Integer pageSize) {
        return matchService.findMatchesByMatchIds(user.getParticipatedMatches(), PageRequest.of(page, pageSize), false, null);
    }

    public Integer organizedMatchSize(User user) {
        return (user.getOrganizedMatches() == null)? 0 : user.getOrganizedMatches().size();
    }

    public Integer participatedMatchSize(User user) {
        return (user.getParticipatedMatches() == null)? 0 : user.getParticipatedMatches().size();
    }
}
