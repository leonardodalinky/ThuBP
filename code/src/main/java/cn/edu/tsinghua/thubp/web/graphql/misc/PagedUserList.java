package cn.edu.tsinghua.thubp.web.graphql.misc;

import cn.edu.tsinghua.thubp.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagedUserList {
    private int page;
    private int pageSize;
    private int totalSize;
    private List<User> list;
}