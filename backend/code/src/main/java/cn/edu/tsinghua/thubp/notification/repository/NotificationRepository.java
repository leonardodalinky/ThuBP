package cn.edu.tsinghua.thubp.notification.repository;

import cn.edu.tsinghua.thubp.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author Link
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, Integer> {
    List<Notification> findAllByNotificationIdIn(Collection<String> notificationIds, Pageable pageable);
}
