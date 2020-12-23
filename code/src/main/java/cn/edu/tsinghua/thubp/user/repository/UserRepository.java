package cn.edu.tsinghua.thubp.user.repository;

import cn.edu.tsinghua.thubp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Link
 */
@Repository
public interface UserRepository extends MongoRepository<User, Integer> {
    Optional<User> findByThuId(String username);
    Optional<User> findByUserId(String userId);
    @Query("{ 'username':{$regex:?0,$options:'i'} }")
    Page<User> findAllByUsernameRegex(String regex, Pageable pageable);
    List<User> findByUserIdIn(List<String> userIds);
    User findByUsername(String username);

    @Transactional(rollbackFor = Exception.class)
    void deleteByThuId(String thuId);
    @Transactional(rollbackFor = Exception.class)
    void deleteByUserId(String userId);

    boolean existsByThuId(String thuId);
    boolean existsByUserId(String userId);
}