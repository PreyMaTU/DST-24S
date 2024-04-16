package dst.ass2.aop.sample;

import java.util.logging.Logger;

public class LoggingPluginExecutable extends AbstractPluginExecutable {
    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(LoggingPluginExecutable.class
        .getName());
}
