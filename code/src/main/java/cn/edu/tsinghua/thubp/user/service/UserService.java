package cn.edu.tsinghua.thubp.user.service;

//import cn.edu.tsinghua.thubp.user.entity.Role;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.exception.*;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import cn.edu.tsinghua.thubp.web.request.UserRegisterRequest;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    public static final String THUID = "thuId: ";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Transactional(rollbackFor = Exception.class)
    public void save(UserRegisterRequest userRegisterRequest) {
        // TODO: 此处先认为 code 为学号，以供实验
        String thuId = userRegisterRequest.getCode();
        userRepository.findByThuId(thuId).ifPresent(__ -> {
            throw new UserThuIdAlreadyExistException(ImmutableMap.of(THUID, thuId));
        });
        // TODO: 真正的学号应该从服务器获取
        User user = User.builder()
                .thuId(userRegisterRequest.getCode())
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
    }

    // TODO 改变抛出错误
    public User findByThuId(String thuId) {
        return userRepository.findByThuId(thuId).orElseThrow(() -> new UserThuIdNotFoundException(ImmutableMap.of(THUID, thuId)));
    }

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UserIdNotFoundException(ImmutableMap.of(THUID, userId)));
    }

    public void update(UserUpdateRequest userUpdateRequest) {
        User user = findByThuId(userUpdateRequest.getUserId());
        if (Objects.nonNull(userUpdateRequest.getUsername())) {
            user.setUsername(userUpdateRequest.getUsername());
        }
        if (Objects.nonNull(userUpdateRequest.getNewPassword())) {
            if (!Objects.nonNull(userUpdateRequest.getOldPassword())) {
                throw new UserOldPwdNotProvidedException(ImmutableMap.of("OldPwd: ", ""));
            }
            if (!bCryptPasswordEncoder.matches(userUpdateRequest.getOldPassword(), user.getPassword())) {
                throw new UserOldPwdNotValidException(ImmutableMap.of("OldPwd: ", userUpdateRequest.getOldPassword()));
            }
            user.setPassword(bCryptPasswordEncoder.encode(userUpdateRequest.getNewPassword()));
        }
        if (Objects.nonNull(userUpdateRequest.getGender())) {
            user.setGender(userUpdateRequest.getGender());
        }
        if (Objects.nonNull(userUpdateRequest.getMobile())) {
            user.setMobile(userUpdateRequest.getMobile());
        }
        if (Objects.nonNull(userUpdateRequest.getEmail())) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (Objects.nonNull(userUpdateRequest.getEnabled())) {
            user.setEnabled(userUpdateRequest.getEnabled());
        }
        userRepository.save(user);
    }


    public void delete(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new UsernameNotFoundException(ImmutableMap.of(THUID, userId));
        }
        userRepository.deleteByUserId(userId);
    }

    public Page<User> getAll(int pageNum, int pageSize) {
        return userRepository.findAll(PageRequest.of(pageNum, pageSize));
    }
}
