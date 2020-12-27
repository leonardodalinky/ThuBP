package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.comment.entity.Comment;
import cn.edu.tsinghua.thubp.comment.service.CommentService;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentResolver implements GraphQLResolver<Comment> {
    private final CommentService commentService;
    private final UserService userService;

    public User issuer(Comment comment) {
        return userService.findByUserId(comment.getIssuerId());
    }

    public String content(Comment comment) {
        return comment.getContent();
    }

    public Comment reply(Comment comment) {
        if (comment.getReplyId() == null) {
            return null;
        }
        return commentService.findByCommentId(comment.getReplyId());
    }
}
