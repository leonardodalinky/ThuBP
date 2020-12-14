package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.comment.entity.Comment;
import cn.edu.tsinghua.thubp.comment.repository.CommentRepository;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.entity.Unit;
import cn.edu.tsinghua.thubp.match.service.UnitService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchResolver implements GraphQLResolver<Match> {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final UnitService unitService;

    public User organizerUser(Match match) {
        return userService.findByUserId(match.getOrganizerUserId());
    }

    public List<User> participants(Match match) {
        return userService.findByUserIdIn(match.getParticipants());
    }

    public List<User> referees(Match match) {
        return userService.findByUserIdIn(match.getReferees());
    }

    public List<Unit> units(Match match) {
        return unitService.findByUnitIds(match.getUnits());
    }

    public List<Comment> comments(Match match, Integer page, Integer pageSize) {
        Page<Comment> commentPage = commentRepository.findAllByCommentIdIn(
                match.getComments(),
                PageRequest.of(page, pageSize)
        );
        return commentPage.getContent();
    }

    public Integer commentSize(Match match) {
        return match.getComments().size();
    }
}
