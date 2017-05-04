package win.sinno.dispatch.engine.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * event in consumer repository
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:05
 */
public class EventInConsumerRepository {

    private static ConcurrentMap<Long, Long> repository;

    private EventInConsumerRepository() {
        repository = new ConcurrentHashMap<>();
    }

    private static class EventInConsumerRepositoryHolder {
        public static final EventInConsumerRepository INSTANCE = new EventInConsumerRepository();
    }

    public static EventInConsumerRepository getInstance() {
        return EventInConsumerRepositoryHolder.INSTANCE;
    }

    public boolean isContain(Long taskId) {
        Long value = repository.putIfAbsent(taskId, taskId);
        return value != null;
    }

    public Long get(Long taskId) {
        return repository.get(taskId);
    }

    public void remove(Long taskId) {
        repository.remove(taskId);
    }

    public void removeAll() {
        repository.clear();
    }


}
