package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.plugin.GameResult;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameFinalResultResolver implements GraphQLResolver<GameResult.GameFinalResult> {
}
