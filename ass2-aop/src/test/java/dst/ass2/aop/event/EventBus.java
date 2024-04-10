package dst.ass2.aop.event;

import dst.ass2.aop.IPluginExecutable;
import org.springframework.aop.support.AopUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateful event bus that stores events triggered by executable plugins.
 * <p/>
 * Note that this implementation is thread safe.
 */
public class EventBus {
    private static final EventBus instance = new EventBus();
    private final List<Event> events = new ArrayList<Event>();

    public static EventBus getInstance() {
        return instance;
    }

    private EventBus() {
    }

    /**
     * Returns all events of the certain type(s).<br/>
     * If no types are specified, all events are returned instead.
     *
     * @param types the event types
     * @return list of all events of the given types.
     */
    public List<Event> getEvents(EventType... types) {
        synchronized (events) {
            if (types == null || types.length == 0) {
                return new ArrayList<Event>(events);
            } else {
                List<Event> list = new ArrayList<Event>();
                for (Event event : events) {
                    for (EventType type : types) {
                        if (type == event.getType()) {
                            list.add(event);
                        }
                    }
                }
                return list;
            }
        }
    }

    /**
     * Resets the event bus by purging the event history.
     */
    public synchronized void reset() {
        synchronized (events) {
            events.clear();
        }
    }

    /**
     * Adds a new event of a certain type triggered by the given plugin.
     *
     * @param type             the event type
     * @param pluginExecutable the plugin that triggered the event
     * @param message          the event message
     */
    @SuppressWarnings("unchecked")
    public void add(EventType type, IPluginExecutable pluginExecutable, String message) {
        add(type, (Class<? extends IPluginExecutable>) AopUtils.getTargetClass(pluginExecutable), message);
    }

    /**
     * Adds a new event of a certain type triggered by a plugin of the given type.
     *
     * @param type       the event type
     * @param pluginType the type of the plugin
     * @param message    the event message
     */
    public void add(EventType type, Class<? extends IPluginExecutable> pluginType, String message) {
        Event event = new Event(type, pluginType, message);
        synchronized (events) {
            events.add(event);
        }
    }

    /**
     * Returns the number of events of a certain type fired by this event bus.
     *
     * @param type the event type
     * @return number of events
     */
    public int count(EventType type) {
        int counter = 0;
        synchronized (events) {
            for (Event event : events) {
                if (event.getType() == type) {
                    counter++;
                }
            }
        }
        return counter;
    }

    /**
     * Returns the number of events fired so far.
     *
     * @return number of events
     */
    public int size() {
        return events.size();
    }

    /**
     * Checks if there was at least one event of a certain type triggered by a plugin with the given full-qualified
     * class name.
     * <p>
     * If {@code pluginType} is {@code null}, the type of the plugin is not checked. The same is true for {@code type}.
     * If all parameters are {@code null}, {@code true} is returned if there is at least one event.
     *
     * @param pluginType the class name of the plugin
     * @param type       the type of the event
     * @return {@code true} if there is at least one event matching the criteria, {@code false} otherwise
     */
    public boolean has(String pluginType, EventType type) {
        synchronized (events) {
            for (Event event : events) {
                if ((pluginType == null || pluginType.equals(event.getPluginClass().getName()))
                    && (type == null || type == event.getType())) {
                    return true;
                }
            }
        }
        return false;
    }
}
