package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.engine.server.HandlerServer;

/**
 * event executor factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-10 16:00.
 */
public final class EventExecutorFactory {

    public static EventExecutor create(HandlerServer handlerServer, EventExecutorAgent eventExecutorAgent, EventConfig eventConfig) {

        EventFetcher eventFetcher = new EventFetcher(handlerServer);

        EventFilter eventFilter = new EventFilter(handlerServer.getEventConsumerRepository());

        EventExecutor eventExecutor = new EventExecutor(handlerServer, eventExecutorAgent, eventConfig, eventFilter, eventFetcher);

        return eventExecutor;
    }
}
