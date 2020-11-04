package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * SpringBoot 应用启动入口
 * CommandLineRunner 用于生成一个 root 用户
 * @author Link
 */
@Slf4j
@SpringBootApplication
public class ThubpApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(ThubpApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 清空数据库
        log.info("清空数据库");
        for (String name: mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(name);
        }
        // 初始化一个 admin 用户
        log.info("创建 root 用户");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!userRepository.existsByThuId("2018000000")) {
            User user = User.builder()
                    .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                    .gender(Gender.UNKNOWN)
                    .enabled(true)
                    .username("root")
                    .password(bCryptPasswordEncoder.encode("root"))
                    .role(RoleType.ROOT)
                    .mobile("10000000000")
                    .email("thubp@tsinghua.edu.cn")
                    .thuId("2018000000")
                    .build();
            userRepository.save(user);
        }
    }
}
