package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.comment.entity.Comment;
import cn.edu.tsinghua.thubp.comment.repository.CommentRepository;
import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.service.GameService;
import cn.edu.tsinghua.thubp.plugin.GameResult;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameResolver implements GraphQLResolver<Game> {
    private final UserService userService;
    private final GameService gameService;
    private final CommentRepository commentRepository;

    public User unit0(Game game) {
        return userService.findByUserId(game.getUnit0());
    }

    public User unit1(Game game) {
        if (game.getUnit1() == null) {
            return null;
        }
        return userService.findByUserId(game.getUnit1());
    }

    public List<Comment> comments(Game game, Integer page, Integer pageSize) {
        Page<Comment> commentPage = commentRepository.findAllByCommentIdIn(
                game.getComments(),
                PageRequest.of(page, pageSize)
        );
        return commentPage.getContent();
    }
}
