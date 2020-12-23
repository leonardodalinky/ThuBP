package cn.edu.tsinghua.thubp;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.enums.Gender;
import cn.edu.tsinghua.thubp.user.enums.RoleType;
import cn.edu.tsinghua.thubp.user.enums.ThuIdentityType;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import cn.edu.tsinghua.thubp.web.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.util.ServiceConfigurationError;

/**
 * SpringBoot 应用启动入口
 * CommandLineRunner 用于生成一个 root 用户
 * @author Link
 */
@Slf4j
@SpringBootApplication
//@EnableTransactionManagement
public class ThubpApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${spring.profiles.active}")
    private String profile;

    public static void main(String[] args) {
        SpringApplication.run(ThubpApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 检查 properties 文件
//        if (ThubpApplication.class.getClassLoader().getResource("config/config-template.properties") == null &&
//                ThubpApplication.class.getClassLoader().getResource("config/config.properties") == null) {
//            throw new ServiceConfigurationError("运行配置未找到！");
//        }
        // dev 环境下运行命令
        if (profile.equals("dev")) {
            // 清空数据库
            log.info("清空数据库");
            for (String name: mongoTemplate.getCollectionNames()) {
                mongoTemplate.dropCollection(name);
            }
        }
        // 初始化一个 admin 用户
        if (!userRepository.existsByThuId("2018000000")) {
            log.info("创建 root 用户");
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            User user = User.builder()
                    .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                    .gender(Gender.UNKNOWN)
                    .enabled(true)
                    .username("root")
                    .realName("根")
                    .thuIdentityType(ThuIdentityType.EXTERNAL)
                    .password(bCryptPasswordEncoder.encode("root"))
                    .role(RoleType.ROOT)
                    .mobile("10000000000")
                    .email("thubp@tsinghua.edu.cn")
                    .thuId("2018000000")
                    .unreadNotificationCount(0)
                    .build();
            userRepository.save(user);
        }
    }
}
