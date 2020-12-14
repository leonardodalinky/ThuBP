package cn.edu.tsinghua.thubp.comment.enums;

import cn.edu.tsinghua.thubp.match.entity.Game;
import cn.edu.tsinghua.thubp.match.entity.Match;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommentSource {
    MATCH("MATCH", "未开始", "matchId", Match.class),
    GAME("GAME", "未开始", "gameId", Game.class),
    ;

    private final String name;
    private final String description;
    private final String idName;
    private final Class<?> clazz;
}
