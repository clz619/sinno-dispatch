package win.sinno.dispatch.engine.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件处理线程上下文
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-10 16:48.
 */
public class EventThreadContext {

    private static final ThreadLocal<EventThreadContext> localEventThreadContext = new ThreadLocal<>();

    /**
     * event thread context params
     */
    private Map<String, Object> contextParams = new HashMap<>();

    private EventThreadContext() {
    }

    public static EventThreadContext init() {
        EventThreadContext eventThreadContext = localEventThreadContext.get();

        if (eventThreadContext == null) {
            eventThreadContext = new EventThreadContext();

            localEventThreadContext.set(eventThreadContext);
        }
        return eventThreadContext;
    }

    public static void destory() {
        EventThreadContext eventThreadContext = localEventThreadContext.get();
        if (eventThreadContext != null) {
            localEventThreadContext.remove();
        }
    }

    public static void put(String key, Object obj) {
        EventThreadContext eventThreadContext = localEventThreadContext.get();

        if (eventThreadContext == null) {
            eventThreadContext = init();
        }

        eventThreadContext._put(key, obj);
    }

    public static Object get(String key) {
        EventThreadContext eventThreadContext = localEventThreadContext.get();

        if (eventThreadContext == null) {
            eventThreadContext = init();
        }
        return eventThreadContext._get(key);
    }

    public void _put(String key, Object obj) {
        contextParams.put(key, obj);
    }

    public Object _get(String key) {
        return contextParams.get(key);
    }

}
