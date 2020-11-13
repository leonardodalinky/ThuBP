package cn.edu.tsinghua.thubp.user.service;

//import cn.edu.tsinghua.thubp.user.entity.Role;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.enums.ThuIdentityType;
import cn.edu.tsinghua.thubp.user.exception.*;
import cn.edu.tsinghua.thubp.user.misc.ThuAuthResult;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import cn.edu.tsinghua.thubp.web.request.UserRegisterRequest;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    public static final String USERID = "userId";
    public static final String THUID = "thuId";
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final ThuAuthService thuAuthService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Transactional(rollbackFor = Exception.class)
    public String saveLegacy(UserRegisterRequest userRegisterRequest) {
        // TODO: 此处先认为 code 为学号，以供实验
        String thuId = userRegisterRequest.getTicket();
        userRepository.findByThuId(thuId).ifPresent(__ -> {
            throw new UserThuIdAlreadyExistException(ImmutableMap.of(THUID, thuId));
        });
        // TODO: 真正的学号应该从服务器获取
        String userId = sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME);
        User user = User.builder()
                .thuId(thuId)
                .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                .username(userRegisterRequest.getUsername())
                .realName(userRegisterRequest.getUsername())
                .thuIdentityType(ThuIdentityType.STUDENT)
                .password(bCryptPasswordEncoder.encode(userRegisterRequest.getPassword()))
                .role(RoleType.USER)
                .enabled(true)
                .mobile(userRegisterRequest.getMobile())
                .email(userRegisterRequest.getEmail())
                .gender(Gender.UNKNOWN)
                .build();
        userRepository.save(user);
        return userId;
    }

    @Transactional(rollbackFor = Exception.class)
    public String save(String userIp, UserRegisterRequest userRegisterRequest) {
        ThuAuthResult identity = thuAuthService.getThuIdentity(userIp, userRegisterRequest.getTicket());
        String thuId = identity.getThuId();
        userRepository.findByThuId(thuId).ifPresent(__ -> {
            throw new UserThuIdAlreadyExistException(ImmutableMap.of(THUID, thuId));
        });
        String userId = sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME);
        User user = User.builder()
                .thuId(thuId)
                .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                .username(userRegisterRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(userRegisterRequest.getPassword()))
                .role(RoleType.USER)
                .enabled(true)
                .mobile(userRegisterRequest.getMobile())
                .email(userRegisterRequest.getEmail())
                .gender(Gender.UNKNOWN)
                .build();
        userRepository.save(user);
        return userId;
    }

    public User findByThuId(String thuId) {
        return userRepository.findByThuId(thuId)
                .orElseThrow(() -> new UserThuIdNotFoundException(ImmutableMap.of(THUID, thuId)));
    }

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserIdNotFoundException(ImmutableMap.of(THUID, userId)));
    }

    public Page<User> findAllByUsernameRegex(String regex, Pageable pageable) {
        return userRepository.findAllByUsernameRegex(regex, pageable);
    }

    /**
     * 根据 userId 的列表，寻找对应的所有 User
     * @param userIds userId 的列表
     * @return User 的列表
     */
    public List<User> findByUserIdIn(List<String> userIds) {
        return userRepository.findByUserIdIn(userIds);
    }

    public void update(User user, UserUpdateRequest userUpdateRequest) {
        Update update = new Update();
        if (Objects.nonNull(userUpdateRequest.getUsername())) {
            update.set("username", userUpdateRequest.getUsername());
        }
        if (Objects.nonNull(userUpdateRequest.getNewPassword())) {
            if (!Objects.nonNull(userUpdateRequest.getOldPassword())) {
                throw new UserOldPwdNotProvidedException(ImmutableMap.of("OldPwd", ""));
            }
            if (!bCryptPasswordEncoder.matches(userUpdateRequest.getOldPassword(), user.getPassword())) {
                throw new UserOldPwdNotValidException(
                        ImmutableMap.of("OldPwd", userUpdateRequest.getOldPassword())
                );
            }
            // TODO: 沒有新密码的复杂度验证
            update.set("password", bCryptPasswordEncoder.encode(userUpdateRequest.getNewPassword()));
        }
        if (Objects.nonNull(userUpdateRequest.getGender())) {
            user.setGender(userUpdateRequest.getGender());
            update.set("gender", userUpdateRequest.getGender());
        }
        if (Objects.nonNull(userUpdateRequest.getMobile())) {
            user.setMobile(userUpdateRequest.getMobile());
            update.set("mobile", userUpdateRequest.getMobile());
        }
        if (Objects.nonNull(userUpdateRequest.getEmail())) {
            user.setEmail(userUpdateRequest.getEmail());
            update.set("email", userUpdateRequest.getEmail());
        }
//        mongoTemplate.updateFirst(
//                Query.query(Criteria.where("userId").is(user.getUserId())),
//                update,
//                User.class);
        User u = mongoTemplate.findAndModify(
                Query.query(Criteria.where("userId").is(user.getUserId())),
                update,
                User.class);
        if (!Objects.nonNull(u)) {
            throw new UserIdNotFoundException(ImmutableMap.of(USERID, user.getUserId()));
        }
    }

    public void delete(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new UsernameNotFoundException(ImmutableMap.of(THUID, userId));
        }
        userRepository.deleteByUserId(userId);
    }

    /**
     * 获得一个固定大小页的 User
     * @param pageNum 页面序号，从 0 开始
     * @param pageSize 页面大小
     * @return User 页面
     */
    public Page<User> getPage(int pageNum, int pageSize) {
        return userRepository.findAll(PageRequest.of(pageNum, pageSize));
    }

    /**
     * 获得数据库中所有 User
     * @return User 列表
     */
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
