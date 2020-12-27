package cn.edu.tsinghua.thubp.web.graphql.resolver;

import cn.edu.tsinghua.thubp.plugin.api.config.GameConfig;
import com.coxautodev.graphql.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class GameConfigParameterResolver implements GraphQLResolver<GameConfig.ConfigParameter> {
}
