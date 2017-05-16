package win.sinno.dispatch.engine.dispatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dispatch handler factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/15 13:58
 */
public class DispatchHandlerFactory {

    private static Map<String, DispatchHandler> handlerMap = new ConcurrentHashMap<>();

    public static DispatchHandler get(String handler) {

        DispatchHandler dispatchHandler = handlerMap.get(handler);

        if (dispatchHandler == null) {

            synchronized (DispatchHandlerFactory.class) {

                dispatchHandler = handlerMap.get(handler);

                if (dispatchHandler == null) {
                    // TODO
                    // reflect handler .
                }
            }
        }

        return dispatchHandler;
    }
}
