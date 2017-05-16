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
public class EventConsumerRepository {

    private ConcurrentMap<Long, Long> repository = new ConcurrentHashMap<>();

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
