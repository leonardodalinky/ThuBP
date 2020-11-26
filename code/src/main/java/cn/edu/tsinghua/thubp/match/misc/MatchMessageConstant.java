package cn.edu.tsinghua.thubp.match.misc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/notification/match_notifications.properties")
public class MatchMessageConstant {
    @Value("${invite_become_referee.title}")
    public String INVITE_REFEREE_NOTIFICATION_TITLE;
    @Value("${invite_become_referee.content}")
    public String INVITE_REFEREE_NOTIFICATION_CONTENT;
}
