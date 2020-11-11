package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.service.GameService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameQueryResolver implements GraphQLQueryResolver {
    private final GameService gameService;

    public Game findGameById(String gameId) {
        return gameService.findByGameId(gameId);
    }

    public List<Game> findGames(List<String> gameIds) {
        return gameService.findByGameIds(gameIds);
    }
}
