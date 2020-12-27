package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.entity.Round;
import cn.edu.tsinghua.thubp.match.service.RoundService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoundQueryResolver implements GraphQLQueryResolver {
    private final RoundService roundService;

    public Round findRoundById(String roundId) {
        return roundService.findByRoundId(roundId);
    }

    public List<Round> findRounds(List<String> roundIds) {
        return roundService.findByRoundIds(roundIds);
    }
}
