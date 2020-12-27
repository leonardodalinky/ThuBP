package cn.edu.tsinghua.thubp.comment.intf;

import java.util.List;

/**
 * Each commentable class MUST has a field named {@code comments} with type {@code List<String>}.
 */
public interface Commentable {
    List<String> getComments();
}
