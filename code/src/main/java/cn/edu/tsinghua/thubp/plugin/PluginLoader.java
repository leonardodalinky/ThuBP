package cn.edu.tsinghua.thubp.plugin;

import cn.edu.tsinghua.thubp.plugin.internal.example_strategy.OrderedKnockoutStrategy;
import cn.edu.tsinghua.thubp.plugin.internal.game.InternalGameTypes;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@Slf4j
@PropertySource("classpath:config/plugin.properties")
public class PluginLoader {

    @Value("${plugin.directory}")
    @Getter(AccessLevel.PRIVATE)
    private String pluginDirectory;

    @Autowired
    private PluginManager pluginManager;

    @PostConstruct
    public void loadAllPluginsFromDirectory() {
        loadInternalPlugins();
        pluginManager.loadAllPlugins();
    }

    /**
     * 加载内部插件.
     * 之后都是要移动到外面去的.
     */
    private void loadInternalPlugins() {
        pluginManager.registerPluginFromInternal(InternalGameTypes.InternalGameTypesConfig);
        pluginManager.registerPluginFromInternal(OrderedKnockoutStrategy.ExampleStrategyConfig);
    }

}
