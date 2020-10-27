package cn.edu.tsinghua.thubp.user.repository;

import cn.edu.tsinghua.thubp.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Link
 */
@Repository
public interface UserRepository extends MongoRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Transactional(rollbackFor = Exception.class)
    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}