package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.plugin.MatchType;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import cn.edu.tsinghua.thubp.web.graphql.misc.PagedMatchList;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchQueryResolver implements GraphQLQueryResolver {
    private final MatchService matchService;
    private final PluginRegistryService pluginRegistryService;
    private final CurrentUserService currentUserService;

    public Match findMatchById(String matchId, String matchToken) {
        return matchService.findByMatchId(matchId, true, currentUserService.getUserId(), matchToken);
    }

    public PagedMatchList findMatchesByType(@org.jetbrains.annotations.Nullable List<String> typeIds, int page, int pageSize) {
        if (typeIds == null || typeIds.size() == 0) {
            typeIds = pluginRegistryService.getAllMatchTypes().stream().map(MatchType::getMatchTypeId).collect(Collectors.toList());
        }
        return matchService.findAllByMatchTypeIdIn(typeIds, PageRequest.of(page, pageSize),
                true, currentUserService.getUserId());
    }

    public List<Match> findMatches(List<String> matchIds) {
        return matchService.findMatchesByMatchIds(matchIds, null, true, currentUserService.getUserId());
    }
}
