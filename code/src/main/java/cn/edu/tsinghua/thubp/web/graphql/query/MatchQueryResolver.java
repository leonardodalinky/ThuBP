package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.match.entity.Match;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchQueryResolver implements GraphQLQueryResolver {
    private final MatchService matchService;

    public Match findMatchById(String matchId) {
        return matchService.findByMatchId(matchId);
    }

    public List<Match> findMatches(List<String> matchIds) {
        return matchService.findMatchesByMatchIds(matchIds);
    }
}
