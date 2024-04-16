package dst.ass2.aop.event;

import dst.ass2.aop.IPluginExecutable;
import org.springframework.util.Assert;

/**
 * Events triggered by {@link IPluginExecutable}s.
 */
public class Event {
    private final long time = System.currentTimeMillis();
    private Class<? extends IPluginExecutable> pluginClass;
    private EventType type;
    private String message;

    public Event(EventType type, Class<? extends IPluginExecutable> pluginClass, String message) {
        this.type = type;
        this.pluginClass = pluginClass;
        this.message = message;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int pos = stackTrace[1].getMethodName().equals("<init>") ? 1 : 2;
        Assert.state(stackTrace[pos].getMethodName().equals("<init>"), "Invalid Event Creation");
        Assert.state(stackTrace[pos + 1].getClassName().equals(EventBus.class.getName()), "Invalid Event Creation");
        Assert.state(stackTrace[pos + 1].getMethodName().equals("add"), "Invalid Event Creation");
    }

    /**
     * Returns the time when the event occurred.
     *
     * @return the event creation time
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the type of the plugin that triggered the event.
     *
     * @return the plugin type
     */
    public Class<? extends IPluginExecutable> getPluginClass() {
        return pluginClass;
    }

    /**
     * Returns the type of the event.
     *
     * @return the event type
     */
    public EventType getType() {
        return type;
    }

    /**
     * Returns the message of the event
     *
     * @return the event message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Event");
        sb.append("{time=").append(time);
        sb.append(", pluginClass=").append(pluginClass);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
