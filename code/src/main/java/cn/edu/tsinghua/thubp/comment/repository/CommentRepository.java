package cn.edu.tsinghua.thubp.comment.repository;

import cn.edu.tsinghua.thubp.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Link
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, Integer> {
    Page<Comment> findAllByCommentIdIn(List<String> commentIds, Pageable pageable);
}
