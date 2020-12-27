package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Round;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.service.GameService;
import cn.edu.tsinghua.thubp.match.service.UnitService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoundResolver implements GraphQLResolver<Round> {
    private final UnitService unitService;
    private final GameService gameService;

    public List<Unit> units(Round round) {
        return unitService.findByUnitIds(round.getUnits());
    }

    public List<Game> games(Round round) {
        return gameService.findByGameIds(round.getGames());
    }
}
