package cn.edu.tsinghua.thubp.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

@Service
@Slf4j
public class PluginManager {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HashMap<String, Class<? extends PluginBase>> pluginClassMap = new HashMap<>();
    private final HashMap<String, PluginBase> pluginInstanceMap = new HashMap<>();
    @Autowired
    private PluginRegistryService pluginRegistryService;

    private PluginManager() {
    }

    public PluginBase getPlugin(String pluginId) {
        return pluginInstanceMap.get(pluginId);
    }

    /**
     * 注册并实例化一个插件.
     * @param pluginInfo 插件 Info
     * @return 插件是否注册成功
     */
    public synchronized boolean registerPlugin(
            @org.jetbrains.annotations.NotNull PluginInfo pluginInfo) {
        if (this.pluginClassMap.containsKey(pluginInfo.getPluginId())) {
            log.error("Plugin {} already registered.", pluginInfo.getPluginId());
            return false;
        }
        try {
            PluginBase plugin = pluginInfo.getPluginMainClass().newInstance();
            plugin.initPlugin(this,
                    new Registry(pluginRegistryService, pluginInfo.getPluginId()),
                    pluginInfo,
                    LoggerFactory.getLogger(pluginInfo.getPluginId()));
            this.pluginClassMap.put(pluginInfo.getPluginId(), plugin.getClass());
            this.pluginInstanceMap.put(pluginInfo.getPluginId(), plugin);
            log.info("Plugin {} instantiated.", pluginInfo.getPluginId());
            return true;
        } catch (Exception exception) {
            log.error("Exception while loading {}: {}", pluginInfo.getPluginId(), exception.getMessage());
            return false;
        }
    }

    /**
     * 从 URL 中载入一个插件.
     * @param url URL
     * @return 载入过程中是否发生了异常.
     */
    @org.jetbrains.annotations.Nullable
    public String registerPluginFromUrl(URL url) {
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{url})) {
            PluginConfig config = objectMapper.readValue(classLoader.getResource("config.json"), PluginConfig.class);
            Class<? extends PluginBase> clazz = classLoader.loadClass(config.getMainClass()).asSubclass(PluginBase.class);
            PluginInfo pluginInfo = new PluginInfo(config.getPluginId(), config.getName(), clazz, config.getMainClass());
            registerPlugin(pluginInfo);
            return pluginInfo.getPluginId();
        } catch (Exception exception) {
            log.error("Failed to load plugin jar: " + url.getFile());
        }
        return null;
    }

    /**
     * 载入一个内部插件.
     * @param pluginConfig 插件配置
     * @return 载入过程中是否发生了异常.
     */
    @org.jetbrains.annotations.Nullable
    public String registerPluginFromInternal(PluginConfig pluginConfig) {
        try {
            Class<? extends PluginBase> clazz = getClass().getClassLoader().loadClass(pluginConfig.getMainClass()).asSubclass(PluginBase.class);
            PluginInfo pluginInfo = new PluginInfo(pluginConfig.getPluginId(), pluginConfig.getName(), clazz, pluginConfig.getMainClass());
            registerPlugin(pluginInfo);
            return pluginInfo.getPluginId();
        } catch (Exception exception) {
            log.error("Failed to load internal plugin: " + pluginConfig.getMainClass());
        }
        return null;
    }

    /**
     * 加载一个插件.
     *
     * @param pluginId 插件 ID
     * @return 插件是否加载成功. 如果插件不存在或加载失败，返回 {@code false}. 如果插件已经加载或加载成功，返回 {@code true}
     */
    public synchronized boolean loadPlugin(String pluginId) {
        PluginBase plugin = this.pluginInstanceMap.get(pluginId);
        if (plugin == null) {
            return false;
        }
        return loadPlugin(plugin);
    }

    /**
     * 卸载一个插件.
     *
     * @param pluginId 插件 ID
     * @return 插件是否加载成功. 如果插件不存在或加载失败，返回 {@code false}. 如果插件已经加载或加载成功，返回 {@code true}
     */
    public synchronized boolean unloadPlugin(String pluginId) {
        PluginBase plugin = this.pluginInstanceMap.get(pluginId);
        if (plugin == null) {
            return false;
        }
        return unloadPlugin(plugin);
    }

    /**
     * 加载所有的插件.
     * 在服务器最初加载完成时会尝试.
     */
    public synchronized void loadAllPlugins() {
        this.pluginInstanceMap.forEach((pluginId, plugin) -> {
            if (!plugin.isLoadingFailed()) {
                log.info("Loading plugin {}", pluginId);
                 loadPlugin(plugin);
            }
        });
    }

    private synchronized boolean loadPlugin(@org.jetbrains.annotations.NotNull PluginBase plugin) {
        plugin.internalLoad();
        return plugin.isLoaded();
    }

    private synchronized boolean unloadPlugin(@org.jetbrains.annotations.NotNull PluginBase plugin) {
        plugin.internalUnload();
        return !plugin.isLoaded();
    }

}
