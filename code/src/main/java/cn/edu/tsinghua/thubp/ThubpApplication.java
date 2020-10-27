package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * SpringBoot 应用启动入口
 * CommandLineRunner 用于生成一个 root 用户
 * @author Link
 */
@SpringBootApplication
public class ThubpApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ThubpApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //初始化一个 admin 用户
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!userRepository.existsByUsername("root")) {
            User user = User.builder().enabled(true).username("root")
                    .password(bCryptPasswordEncoder.encode("root")).role(RoleType.USER).build();
            userRepository.save(user);
        }
    }
}
