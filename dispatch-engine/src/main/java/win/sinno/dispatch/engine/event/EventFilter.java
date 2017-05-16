package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.engine.repository.EventConsumerRepository;

/**
 * event filter
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 14:22.
 */
public class EventFilter {

    private EventConsumerRepository eventConsumerRepository;

    public EventFilter(EventConsumerRepository eventConsumerRepository) {
        this.eventConsumerRepository = eventConsumerRepository;
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isAccept(Long taskId) {
        return !eventConsumerRepository.isContain(taskId);
    }

}
