package dst.ass3.event;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A SinkFunction that collects objects into a queue located in a shared global state. Each collector accesses a
 * specific key in the shared state.
 *
 * @param <T> the sink input type
 */
public class StaticQueueSink<T> implements SinkFunction<T> {

    private static final long serialVersionUID = -3965500756295835669L;

    private static Map<String, BlockingQueue<?>> state = new ConcurrentHashMap<>();

    private String key;

    public StaticQueueSink(String key) {
        this.key = key;
    }

    @Override
    public void invoke(T value, Context context) throws Exception {
        get().add(value);
    }

    public void clear() {
        get().clear();
    }

    public List<T> take(int n) {
        List<T> list = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            try {
                list.add(get().take());
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while accessing queue", e);
            }
        }

        return list;
    }

    public T take() {
        try {
            return get().take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public T poll(long ms) {
        try {
            return get().poll(ms, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public synchronized BlockingQueue<T> get() {
        return get(key);
    }

    @SuppressWarnings("unchecked")
    private static <R> BlockingQueue<R> get(String key) {
        return (BlockingQueue) state.computeIfAbsent(key, k -> new LinkedBlockingQueue<>());
    }

    public static void clearAll() {
        state.clear();
    }
}
