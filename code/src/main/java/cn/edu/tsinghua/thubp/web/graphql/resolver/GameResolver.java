package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameResolver implements GraphQLResolver<Game> {
    private final UserService userService;

    public User unit0(Game game) {
        return userService.findByUserId(game.getUnit0());
    }

    public User unit1(Game game) {
        if (game.getUnit1() == null) {
            return null;
        }
        return userService.findByUserId(game.getUnit1());
    }
}
