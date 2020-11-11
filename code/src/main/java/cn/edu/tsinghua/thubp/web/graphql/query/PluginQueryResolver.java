package cn.edu.tsinghua.thubp.web.graphql.query;

import cn.edu.tsinghua.thubp.plugin.MatchType;
import cn.edu.tsinghua.thubp.plugin.PluginRegistryService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PluginQueryResolver implements GraphQLQueryResolver {
    private final PluginRegistryService pluginRegistryService;

    public List<MatchType> listMatchTypes() {
        return ImmutableList.copyOf(pluginRegistryService.getAllMatchTypes());
    }
}
