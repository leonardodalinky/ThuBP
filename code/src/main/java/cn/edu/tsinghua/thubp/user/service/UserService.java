package cn.edu.tsinghua.thubp.user.service;

//import cn.edu.tsinghua.thubp.user.entity.Role;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.exception.UsernameAlreadyExistException;
import cn.edu.tsinghua.thubp.user.exception.UsernameNotFoundException;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import cn.edu.tsinghua.thubp.web.request.UserRegisterRequest;
import cn.edu.tsinghua.thubp.web.request.UserUpdateRequest;
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

    public static final String USERNAME = "username:";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void save(UserRegisterRequest userRegisterRequest) {
        userRepository.findByUsername(userRegisterRequest.getUsername()).ifPresent(__ -> {
            throw new UsernameAlreadyExistException(ImmutableMap.of(USERNAME, userRegisterRequest.getUsername()));
        });
        User user = User.builder().username(userRegisterRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(userRegisterRequest.getPassword()))
                .role(RoleType.USER)
                .enabled(true)
                .mobile(userRegisterRequest.getMobile())
                .email(userRegisterRequest.getEmail())
                .build();
        userRepository.save(user);
    }

    public User find(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(ImmutableMap.of(USERNAME, username)));
    }

    public void update(UserUpdateRequest userUpdateRequest) {
        User user = find(userUpdateRequest.getUsername());
        if (Objects.nonNull(userUpdateRequest.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(userUpdateRequest.getPassword()));
        }
        if (Objects.nonNull(userUpdateRequest.getEnabled())) {
            user.setEnabled(userUpdateRequest.getEnabled());
        }
        userRepository.save(user);
    }


    public void delete(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException(ImmutableMap.of(USERNAME, username));
        }
        userRepository.deleteByUsername(username);
    }

    public Page<User> getAll(int pageNum, int pageSize) {
        return userRepository.findAll(PageRequest.of(pageNum, pageSize));
    }
}
