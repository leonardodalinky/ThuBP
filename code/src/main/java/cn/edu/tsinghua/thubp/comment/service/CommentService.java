package cn.edu.tsinghua.thubp.comment.service;

import cn.edu.tsinghua.thubp.comment.entity.Comment;
import cn.edu.tsinghua.thubp.comment.exception.CommentErrorCode;
import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.match.entity.Match;
import cn.edu.tsinghua.thubp.match.exception.MatchErrorCode;
import cn.edu.tsinghua.thubp.tool.moderation.ModerationService;
import cn.edu.tsinghua.thubp.tool.moderation.Result;
import cn.edu.tsinghua.thubp.tool.moderation.ResultType;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.web.request.CommentModifyRequest;
import cn.edu.tsinghua.thubp.web.request.CommentRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentService {
    public static final String USER_ID = "userId";
    public static final String MATCH_ID = "matchId";
    public static final String COMMENT_ID = "commentId";
    public static final String DETAIL = "detail";

    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final ModerationService moderationService;

    /**
     * 创建一个评论
     * @param issuerId 评论者的 ID
     * @param content 评论内容
     * @param replyId 回复的评论 ID，可为 {@code null}。
     * @return 新创建的评论 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String createComment(@NonNull String issuerId, @NonNull String content, @Nullable String replyId) {
        Comment comment = Comment.builder()
                .commentId(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME))
                .issuerId(issuerId)
                .content(content)
                .replyId(replyId)
                .build();
        mongoTemplate.save(comment);
        return comment.getCommentId();
    }

    /**
     * 评论赛事
     * @param user 用户
     * @param matchId 赛事 ID
     * @param commentRequest 评论的请求
     * @return 新的评论的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String commentMatch(User user, String matchId, CommentRequest commentRequest) {
        // 如果是回复评论,检查比赛下是否有该评论
        if (commentRequest.getReplyId() != null) {
            boolean ret = mongoTemplate.exists(Query.query(
                    new Criteria().andOperator(
                            Criteria.where("matchId").is(matchId),
                            Criteria.where("comments").all(commentRequest.getReplyId())
                    )
            ), Match.class);
            if (!ret) {
                throw new CommonException(CommentErrorCode.MATCH_REPLY_NOT_FOUND,
                        ImmutableMap.of(MATCH_ID, matchId));
            }
        }
        // 文本审核
        // 不需要异步，因为需要结果
        Result result = moderationService.moderate(commentRequest.getContent());
        if (result.getType() != ResultType.PASS) {
            throw new CommonException(CommentErrorCode.MODERATION_FAILED, ImmutableMap.of(DETAIL, result.getDetail()));
        }
        // 创建评论
        String commentId = createComment(user.getUserId(), commentRequest.getContent(), commentRequest.getReplyId());
        mongoTemplate.updateFirst(Query.query(
                Criteria.where("matchId").is(matchId)
        ), new Update().push("comments", commentId), Match.class);

        return commentId;
   }

    /**
     * 修改评论
     * @param user 用户
     * @param commentId 评论 ID
     * @param commentModifyRequest 修改评论的请求
     * @return 当前评论的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String modifyComment(User user, String commentId, CommentModifyRequest commentModifyRequest) {
        // 文本审核
        // 不需要异步，因为需要结果
        Result result = moderationService.moderate(commentModifyRequest.getContent());
        if (result.getType() != ResultType.PASS) {
            throw new CommonException(CommentErrorCode.MODERATION_FAILED, ImmutableMap.of(DETAIL, result.getDetail()));
        }
        // 修改评论
        long cnt = mongoTemplate.updateFirst(Query.query(
                new Criteria().andOperator(
                        Criteria.where("commentId").is(commentId),
                        Criteria.where("issuerId").is(user.getUserId())
                )
        ), new Update().set("content", commentModifyRequest.getContent()), Comment.class).getModifiedCount();
        if (cnt != 1) {
            throw new CommonException(CommentErrorCode.COMMENT_NOT_FOUND, ImmutableMap.of(COMMENT_ID, commentId));
        }
        return commentId;
    }

    /**
     * 删除评论
     * @param user 用户
     * @param commentId 评论 ID
     * @return 当前评论的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String deleteComment(User user, String commentId) {
        // 修改评论
        long cnt = mongoTemplate.remove(Query.query(
                new Criteria().andOperator(
                        Criteria.where("commentId").is(commentId),
                        Criteria.where("issuerId").is(user.getUserId())
                )
        ), Comment.class).getDeletedCount();
        if (cnt != 1) {
            throw new CommonException(CommentErrorCode.COMMENT_NOT_FOUND, ImmutableMap.of(COMMENT_ID, commentId));
        }
        return commentId;
    }

    /**
     * 根据 commentId 查找赛事.
     * @param commentId 赛事 ID
     * @return 对应的赛事
     */
    public Comment findByCommentId(String commentId) {
        Comment comment = mongoTemplate.findOne(Query.query(
                Criteria.where("commentId").is(commentId)
        ), Comment.class);
        if (comment == null) {
            throw new CommonException(CommentErrorCode.COMMENT_NOT_FOUND, ImmutableMap.of(COMMENT_ID, commentId));
        }
        return comment;
    }
}
