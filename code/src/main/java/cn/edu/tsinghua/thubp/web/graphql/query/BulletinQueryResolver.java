package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.bulletin.entity.BulletinEntry;
import cn.edu.tsinghua.thubp.bulletin.service.BulletinService;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.service.MatchService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BulletinQueryResolver implements GraphQLQueryResolver {
    private final BulletinService bulletinService;
    private final MatchService matchService;

    public List<Match> getBulletin() {
        List<BulletinEntry> entries = bulletinService.getAll();
        List<String> matchIds = entries.stream().map(BulletinEntry::getMatchId).collect(Collectors.toList());
        // 此处不需要检查 publicShow，因为公告板中的内容一定是公开的
        return matchService.findMatchesByMatchIds(matchIds, null, false, null);
    }
}
