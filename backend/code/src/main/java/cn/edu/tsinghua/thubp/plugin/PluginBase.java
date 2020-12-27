package cn.edu.tsinghua.thubp.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public abstract class PluginBase {

    private PluginManager pluginManager;
    private Registry registry;
    private PluginInfo pluginInfo;
    private Logger logger;
    private boolean loaded;
    @Getter(AccessLevel.PACKAGE)
    private boolean loadingFailed;

    /**
     * 初始化插件. 由 PluginManager 调用.
     *
     * @param pluginInfo 插件基本信息
     * @param logger     logger
     */
    final void initPlugin(PluginManager pluginManager,
                          Registry registry,
                          PluginInfo pluginInfo,
                          Logger logger) {
        this.registry = registry;
        this.pluginInfo = pluginInfo;
        this.logger = logger;
    }

    final void internalLoad() {
        if (!this.loaded) {
            try {
                this.onLoad();
                this.loaded = true;
                this.loadingFailed = false;
            } catch (Exception exception) {
                if (this.logger != null) {
                    this.logger.error("Error loading plugin {}: {}", this.pluginInfo.getPluginId(), exception.getMessage());
                }
                this.loadingFailed = true;
            }
        }
    }

    final void internalUnload() {
        if (this.loaded) {
            try {
                this.onUnload();
                this.loaded = false;
            } catch (Exception exception) {
                if (this.logger != null) {
                    this.logger.error("Error unloading plugin {}: {}", this.pluginInfo.getPluginId(), exception.getMessage());
                }
            }
            this.loaded = false;
        }
    }

    /**
     * 当插件被加载时调用.
     */
    public void onLoad() {
    }

    /**
     * 当插件被卸载时调用.
     */
    public void onUnload() {
    }

}
