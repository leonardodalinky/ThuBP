package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.plugin.MatchType;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchTypeResolver implements GraphQLResolver<MatchType> {
}
