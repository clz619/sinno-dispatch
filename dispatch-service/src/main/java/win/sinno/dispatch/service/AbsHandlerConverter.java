package win.sinno.dispatch.service;

import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchHandlerConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * dispatch handler converter
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/16 14:29
 */
public abstract class AbsHandlerConverter implements DispatchHandlerConverter {

    private Map<String, DispatchHandler> handlerMap = new HashMap<>();

    /**
     * converter
     *
     * @param handler
     * @return DispatchHandler
     */
    @Override
    public DispatchHandler converter(String handler) {

        DispatchHandler dispatchHandler = handlerMap.get(handler);

        if (dispatchHandler == null) {
            synchronized (this) {
                dispatchHandler = handlerMap.get(handler);

                if (dispatchHandler == null) {
                    dispatchHandler = get(handler);

                    handlerMap.put(handler, dispatchHandler);
                }
            }
        }

        return dispatchHandler;
    }

    public abstract DispatchHandler get(String handler);
}
