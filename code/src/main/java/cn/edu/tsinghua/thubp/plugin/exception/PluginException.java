package cn.edu.tsinghua.thubp.plugin.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PluginException extends Exception {
    public PluginException(String message) {
        super(message);
    }

    public PluginException(Exception cause) {
        super(cause);
    }
}
