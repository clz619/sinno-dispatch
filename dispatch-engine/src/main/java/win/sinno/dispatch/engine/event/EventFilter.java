package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.engine.repository.EventInConsumerRepository;

/**
 * event filter
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 14:22.
 */
public class EventFilter {

    private EventInConsumerRepository eventInConsumerRepository;

    public EventFilter(EventInConsumerRepository eventInConsumerRepository) {
        this.eventInConsumerRepository = eventInConsumerRepository;
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isAccept(Long taskId) {
        return !eventInConsumerRepository.isContain(taskId);
    }

}
