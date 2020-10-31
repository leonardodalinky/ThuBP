package cn.edu.tsinghua.thubp.web.graphql;

import cn.edu.tsinghua.thubp.match.entity.Match;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MatchResolver implements GraphQLResolver<Match> {

}
