package cn.edu.tsinghua.thubp.web.graphql;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.repository.UserRepository;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserResolver implements GraphQLResolver<User> {
    private final UserRepository userRepository;

    public String gender(User user) {
        return user.getGender().getName();
    }
}
