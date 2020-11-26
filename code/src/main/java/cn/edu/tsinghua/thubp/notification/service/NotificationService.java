package cn.edu.tsinghua.thubp.notification.service;

import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.notification.entity.Notification;
import cn.edu.tsinghua.thubp.notification.enums.NotificationTag;
import cn.edu.tsinghua.thubp.notification.exception.NotificationErrorCode;
import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.exception.UserErrorCode;
import cn.edu.tsinghua.thubp.user.exception.UserIdNotFoundException;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationService {
    public static final String SYSTEM_ID = "1";
    public static final String USER_ID = "userId";
    public static final String FROM_USER_ID = "fromUserId";
    public static final String NOTIFICATION_ID = "notificationId";

    public static final String PLACEHOLDER_RECEIVER_USERNAME = "{receiver.username}";
    public static final String PLACEHOLDER_SENDER_USERNAME = "{sender.username}";

    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;

    /**
     * 给 userId 的用户发送通知
     * @param userId 接收的用户 ID
     * @param fromUserId 发送的用户 ID
     * @param title 通知的标题
     * @param content 通知的内容
     * @return 新通知的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String sendNotification(String userId, String fromUserId, String title, String content, NotificationTag tag) {
        // check userId
        boolean ret = mongoTemplate.exists(Query.query(
                Criteria.where("userId").is(userId)
        ), User.class);
        if (!ret) {
            throw new UserIdNotFoundException(ImmutableMap.of(USER_ID, userId));
        }
        // check fromUserId
        ret = mongoTemplate.exists(Query.query(
                Criteria.where("userId").is(fromUserId)
        ), User.class);
        if (!ret) {
            throw new UserIdNotFoundException(ImmutableMap.of(FROM_USER_ID, fromUserId));
        }
        // build notification and save
        String notificationId = sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME);
        Notification notification = Notification.builder()
                .notificationId(notificationId)
                .fromUserId(fromUserId)
                .toUserId(userId)
                .tag(tag)
                .title(title)
                .content(content)
                .isRead(false)
                .build();
        mongoTemplate.save(notification);
        // update user relevant fields
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("userId").is(userId)
                ), new Update().push("notifications", notificationId).inc("unreadNotificationCount", 1)
                , User.class).getModifiedCount();
        if (cnt == 0) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_FAILED, ImmutableMap.of(
                    USER_ID, userId,
                    FROM_USER_ID, fromUserId
            ));
        }
        return notificationId;
    }

    /**
     * 给一些用户群发通知.
     * 不存在的用户会直接被忽略.
     * @param userIds 接收的用户 ID 列表
     * @param fromUserId 发送的用户 ID
     * @param title 通知的标题
     * @param content 通知的内容
     * @return 成功发送的用户 ID 列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<String> sendNotificationToMultipleUsers(List<String> userIds, String fromUserId, String title, String content, NotificationTag tag) {
        // check fromUserId
        User sender = mongoTemplate.findOne(Query.query(
                Criteria.where("userId").is(fromUserId)
        ), User.class);
        if (sender == null) {
            throw new UserIdNotFoundException(ImmutableMap.of(FROM_USER_ID, fromUserId));
        }
        // check userId
        List<User> users = mongoTemplate.find(Query.query(
                Criteria.where("userId").in(userIds)
        ), User.class);
        List<String> notificationIds = new ArrayList<>();
        for (User user : users) {
            // build notification and save
            String notificationId = sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME);
            String userContent = content.replace(PLACEHOLDER_RECEIVER_USERNAME, user.getUsername())
                                        .replace(PLACEHOLDER_SENDER_USERNAME, sender.getUsername());
            Notification notification = Notification.builder()
                    .notificationId(notificationId)
                    .fromUserId(fromUserId)
                    .toUserId(user.getUserId())
                    .tag(tag)
                    .title(title)
                    .content(userContent)
                    .isRead(false)
                    .build();
            mongoTemplate.save(notification);
            notificationIds.add(user.getUserId());
            // update user relevant fields
            long cnt = mongoTemplate.updateFirst(Query.query(
                    Criteria.where("userId").is(user.getUserId())
                    ), new Update().push("notifications", notificationId).inc("unreadNotificationCount", 1)
                    , User.class).getModifiedCount();
            if (cnt == 0) {
                mongoTemplate.remove(notification);
            }
        }
        return notificationIds;
    }

    /**
     * 给 userId 的用户发送系统通知
     * @param userId 接收的用户 ID
     * @param title 通知的标题
     * @param content 通知的内容
     * @return 新通知的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String sendNotificationFromSystem(String userId, String title, String content, NotificationTag tag) {
        return sendNotification(userId, SYSTEM_ID, title, content, tag);
    }

    public String getUnreadCount(String userId) {
        List<Integer> unread = mongoTemplate.findDistinct(Query.query(
                Criteria.where("userId").is(userId)
        ), "unreadNotificationCount", User.class, Integer.class);
        if (unread.size() != 1) {
            throw new CommonException(UserErrorCode.USER_NOT_FOUND, ImmutableMap.of(
                    USER_ID, userId
            ));
        }
        return unread.get(0).toString();
    }

    /**
     * 将用户所有的通知标为已读
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void readAllNotification(String userId) {
        // check
        boolean ret = mongoTemplate.exists(Query.query(
                Criteria.where("userId").is(userId)
        ), User.class);
        if (!ret) {
            throw new CommonException(UserErrorCode.USER_NOT_FOUND, ImmutableMap.of(
                    USER_ID, userId
            ));
        }
        // modify
        mongoTemplate.updateMulti(Query.query(
                Criteria.where("toUserId").is(userId)
        ), new Update().set("isRead", true), Notification.class);
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("userId").is(userId)
        ), new Update().set("unreadNotificationCount", 0), User.class).getMatchedCount();
        if (cnt == 0) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_FAILED, ImmutableMap.of(
                    USER_ID, userId
            ));
        }
    }

    /**
     * 将用户某个通知设为已读
     * @param userId 用户
     * @param notificationId 通知 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void readNotification(String userId, String notificationId) {
        // check
        boolean ret = mongoTemplate.exists(Query.query(
                new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("notifications").all(notificationId)
                )
        ), User.class);
        if (!ret) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_NOT_FOUND, ImmutableMap.of(
                    USER_ID, userId,
                    NOTIFICATION_ID, notificationId
            ));
        }
        // modify
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("notificationId").is(notificationId)
        ), new Update().set("isRead", true), Notification.class).getMatchedCount();
        if (cnt == 0) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_FAILED, ImmutableMap.of(
                    USER_ID, userId,
                    NOTIFICATION_ID, notificationId
            ));
        }
        // update unread count
        updateUnreadCount(userId);
    }

    /**
     * 删除用户的一些通知
     * @param userId 用户
     * @param notifications 待删除通知 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotifications(String userId, List<String> notifications) {
        // check
        boolean ret = mongoTemplate.exists(Query.query(
                new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("notifications").all(notifications)
                )
        ), User.class);
        if (!ret) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_NOT_FOUND, ImmutableMap.of(
                    USER_ID, userId,
                    NOTIFICATION_ID, notifications
            ));
        }
        // delete
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("userId").is(userId)
        ), new Update().pullAll("notifications", notifications.toArray()), User.class).getModifiedCount();
        if (cnt == 0) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_NOT_FOUND, ImmutableMap.of(
                    USER_ID, userId,
                    NOTIFICATION_ID, notifications
            ));
        }
        cnt = mongoTemplate.remove(Query.query(
                Criteria.where("notificationId").in(notifications)
        ), Notification.class).getDeletedCount();
        if (cnt != notifications.size()) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_NOT_FOUND, ImmutableMap.of(
                    USER_ID, userId,
                    NOTIFICATION_ID, notifications
            ));
        }
        // update unread
        updateUnreadCount(userId);
    }

    /**
     * 更新一个用户的所有未读数量
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUnreadCount(String userId) {
        long unreadCount = mongoTemplate.count(Query.query(
                new Criteria().andOperator(
                        Criteria.where("toUserId").is(userId),
                        Criteria.where("isRead").is(false)
                )
        ), Notification.class);
        long cnt = mongoTemplate.updateFirst(Query.query(
                Criteria.where("userId").is(userId)
        ), new Update().set("unreadNotificationCount", unreadCount), User.class).getMatchedCount();
        if (cnt == 0) {
            throw new CommonException(NotificationErrorCode.NOTIFICATION_FAILED, ImmutableMap.of(
                    USER_ID, userId
            ));
        }
    }
}
