package cn.edu.tsinghua.thubp.user.service;

import cn.edu.tsinghua.thubp.common.config.GlobalConfig;
import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.util.AutoModifyUtil;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    public static final String USERID = "userId";
    public static final String THUID = "thuId";
    public static final String USERNAME = "username";
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final ThuAuthService thuAuthService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final GlobalConfig globalConfig;

    @Transactional(rollbackFor = Exception.class)
    public String saveLegacy(UserRegisterRequest userRegisterRequest) {
        // 重名检测
        checkUsername(userRegisterRequest.getUsername());
        // TODO: 此处先认为 ticket 为学号，以供实验
        String thuId = userRegisterRequest.getTicket();
        userRepository.findByThuId(thuId).ifPresent(__ -> {
            throw new UserThuIdAlreadyExistException(ImmutableMap.of(THUID, thuId));
        });
        // TODO: 真正的学号应该从服务器获取
        String userId = sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME);
        User user = User.builder()
                .thuId(thuId)
                .userId(userId)
                .username(userRegisterRequest.getUsername())
                .description(userRegisterRequest.getDescription())
                .realName(userRegisterRequest.getUsername())
                .thuIdentityType(ThuIdentityType.STUDENT)
                .password(bCryptPasswordEncoder.encode(userRegisterRequest.getPassword()))
                .role(RoleType.USER)
                .enabled(true)
                .mobile(userRegisterRequest.getMobile())
                .email(userRegisterRequest.getEmail())
                .gender(Gender.UNKNOWN)
                .unreadNotificationCount(0)
                .build();
        userRepository.save(user);
        return userId;
    }

    @Transactional(rollbackFor = Exception.class)
    public String save(String userIp, UserRegisterRequest userRegisterRequest) {
        // 重名检测
        checkUsername(userRegisterRequest.getUsername());
        // 服务器交互
        ThuAuthResult identity = thuAuthService.getThuIdentity(userIp, userRegisterRequest.getTicket());
        String thuId = identity.getThuId();
        userRepository.findByThuId(thuId).ifPresent(__ -> {
            throw new UserThuIdAlreadyExistException(ImmutableMap.of(THUID, thuId));
        });
        String userId = sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME);
        String userEmail = userRegisterRequest.getEmail();
        if (userEmail == null) {
            userEmail = identity.getEmail();
        }
        User user = User.builder()
                .thuId(thuId)
                .userId(userId)
                .username(userRegisterRequest.getUsername())
                .description(userRegisterRequest.getDescription())
                .password(bCryptPasswordEncoder.encode(userRegisterRequest.getPassword()))
                .role(RoleType.USER)
                .realName(identity.getRealName())
                .thuIdentityType(identity.getIdentityType())
                .enabled(true)
                .mobile(userRegisterRequest.getMobile())
                .email(userEmail)
                .gender(Gender.UNKNOWN)
                .unreadNotificationCount(0)
                .build();
        userRepository.save(user);
        return userId;
    }

    public User findByThuId(String thuId) {
        return userRepository.findByThuId(thuId)
                .orElseThrow(() -> new UserThuIdNotFoundException(ImmutableMap.of(THUID, thuId)));
    }

    public User findByUserId(@NotNull String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserIdNotFoundException(ImmutableMap.of(THUID, userId)));
    }

    public Page<User> findAllByUsernameRegex(String regex, Pageable pageable) {
        return userRepository.findAllByUsernameRegex(regex, pageable);
    }

    public User findByUsername(@NotNull String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据 userId 的列表，寻找对应的所有 User
     * @param userIds userId 的列表
     * @return User 的列表
     */
    public List<User> findByUserIdIn(@NotNull List<String> userIds) {
        return userRepository.findByUserIdIn(userIds);
    }

    public void update(User user, UserUpdateRequest userUpdateRequest) throws MalformedURLException {
        // 重名检测，除非是原来的名字
        if (!user.getUsername().equals(userUpdateRequest.getUsername())) {
            checkUsername(userUpdateRequest.getUsername());
        }
        // 自动修改部份属性
        AutoModifyUtil.autoModify(userUpdateRequest, user);
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
            user.setPassword(bCryptPasswordEncoder.encode(userUpdateRequest.getNewPassword()));
        }
        if (Objects.nonNull(userUpdateRequest.getAvatar())) {
            user.setAvatar(new URL("http", globalConfig.getQiNiuHost(), "/" + userUpdateRequest.getAvatar()));
        }
        // 保存
        mongoTemplate.save(user);
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

    /**
     * 重名检测
     * @param username 待检测的用户名
     */
    private void checkUsername(@Nullable String username) {
        // 重名检测
        if (username != null) {
            User u = mongoTemplate.findOne(Query.query(
                    Criteria.where("username").is(username)
            ), User.class);
            if (u != null) {
                throw new CommonException(UserErrorCode.USER_NAME_ALREADY_EXIST,
                        ImmutableMap.of(USERNAME, username));
            }
        }
    }
}
