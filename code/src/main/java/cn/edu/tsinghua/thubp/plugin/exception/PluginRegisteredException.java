package cn.edu.tsinghua.thubp.plugin.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PluginRegisteredException extends PluginException {
    public PluginRegisteredException(String message) {
        super(message);
    }
}
