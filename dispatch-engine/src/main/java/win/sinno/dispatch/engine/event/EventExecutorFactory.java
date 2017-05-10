package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.engine.ScheduleServer;
import win.sinno.dispatch.engine.repository.EventInConsumerRepository;

/**
 * event executor factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-10 16:00.
 */
public final class EventExecutorFactory {

    public static EventExecutor createExecutor(EventConfig eventConfig) {

        EventFetcher eventFetcher = new EventFetcher(ScheduleServer.getInstance().getDispatchService());

        EventFilter eventFilter = new EventFilter(EventInConsumerRepository.getInstance());

        EventExecutor eventExecutor = new EventExecutor(eventConfig, eventFilter, eventFetcher);

        return eventExecutor;
    }
}
