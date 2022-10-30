package run.halo.tocbot;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * tocbot js 集成
 *
 * @author liuzhihang
 * @date 2022/10/23
 */
@Component
public class TocbotPlugin extends BasePlugin {

    public TocbotPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
