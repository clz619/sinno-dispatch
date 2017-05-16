package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchHandlerFactory;
import win.sinno.dispatch.engine.server.HandlerServer;

/**
 * event consumer factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-10 16:22.
 */
public final class EventConsumerFactory {

    public static EventConsumer create(HandlerServer handlerServer, EventExecutorAgent eventExecutorManager, DispatchTaskEntity dispatchTaskEntity, EventConfig eventConfig) {

        DispatchHandler dispatchHandler = DispatchHandlerFactory.get(dispatchTaskEntity.getHandler());

        EventConsumer eventConsumer = new EventConsumer(handlerServer, eventExecutorManager, dispatchTaskEntity, dispatchHandler, handlerServer.getDispatchResultService(), eventConfig.getHandlerIdentifyCode());

        return eventConsumer;
    }
}
