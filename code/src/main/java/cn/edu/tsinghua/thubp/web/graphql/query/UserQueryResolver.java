package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.user.entity.User;
import cn.edu.tsinghua.thubp.user.service.UserService;
import cn.edu.tsinghua.thubp.web.graphql.misc.PagedMatchList;
import cn.edu.tsinghua.thubp.web.graphql.misc.PagedUserList;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserQueryResolver implements GraphQLQueryResolver {

    private final UserService userService;

    public PagedUserList findUserByFuzzy(String username, Integer page, Integer pageSize) {
        Page<User> userPage = userService.findAllByUsernameRegex(
                ".*" + username + ".*",
                PageRequest.of(page, pageSize)
        );
        return PagedUserList.builder()
                .page(userPage.getNumber())
                .pageSize(userPage.getNumberOfElements())
                .totalSize((int)userPage.getTotalElements())
                .list(userPage.getContent())
                .build();
    }

    public List<User> findUserById(List<String> userIds) {
        return userService.findByUserIdIn(userIds);
    }
}
