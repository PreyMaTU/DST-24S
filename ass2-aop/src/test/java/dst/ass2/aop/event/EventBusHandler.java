package dst.ass2.aop.event;

import dst.ass2.aop.IPluginExecutable;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Logging handler that uses the {@link EventBus} for publishing events.
 */
public class EventBusHandler extends Handler {
    @SuppressWarnings("unchecked")
    @Override
    public void publish(LogRecord record) {
        if (record.getLoggerName().endsWith("PluginExecutable") && record.getMessage().contains("PluginExecutable")) {
            try {
                Class<? extends IPluginExecutable> clazz = (Class<? extends IPluginExecutable>) Class.forName(record.getLoggerName());
                EventBus.getInstance().add(EventType.INFO, clazz, record.getSourceClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Simply does nothing.
     */
    @Override
    public void flush() {
    }

    /**
     * Simply does nothing.
     */
    @Override
    public void close() {
    }
}
