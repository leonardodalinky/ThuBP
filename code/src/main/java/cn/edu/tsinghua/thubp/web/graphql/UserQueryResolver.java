package cn.edu.tsinghua.thubp.web.graphql;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserQueryResolver implements GraphQLQueryResolver {

    private final UserService userService;

    public List<User> findUserByFuzzy(String username, Integer page, Integer pageSize) {
//        Page<User> userPage = userService.findAllByUsernameRegex(
//                ".*" + username + ".*",
//                PageRequest.of(page, pageSize)
//        );
//        return userPage.getContent();
        return null;
    }

    public List<User> findUserById(List<String> userIds) {
        return null;
        //return userService.findByUserIdIn(userIds);
    }
}
