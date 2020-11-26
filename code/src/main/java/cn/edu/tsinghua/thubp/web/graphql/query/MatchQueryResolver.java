package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.repository.MatchRepository;
import cn.edu.tsinghua.thubp.match.service.MatchService;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.plugin.MatchType;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
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
    private final MatchRepository matchRepository;

    public Match findMatchById(String matchId) {
        return matchService.findByMatchId(matchId);
    }

    public List<Match> findMatchesByType(@org.jetbrains.annotations.Nullable List<String> typeIds, int page, int pageSize) {
        if (typeIds == null || typeIds.size() == 0) {
            typeIds = pluginRegistryService.getAllMatchTypes().stream().map(MatchType::getMatchTypeId).collect(Collectors.toList());
        }
        return matchRepository.findAllByMatchTypeIdIn(typeIds, PageRequest.of(page, pageSize));
    }

    public List<Match> findMatches(List<String> matchIds) {
        return matchService.findMatchesByMatchIds(matchIds);
    }
}
