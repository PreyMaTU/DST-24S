package dst.ass2.aop;

import dst.ass2.aop.impl.PluginExecutor;

public class PluginExecutorFactory {

    public static IPluginExecutor createPluginExecutor() {
        return new PluginExecutor();
    }

}
