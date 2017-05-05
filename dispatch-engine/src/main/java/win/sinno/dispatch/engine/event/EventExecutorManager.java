package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.engine.repository.EventInConsumerRepository;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * event executor manager
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 17:34.
 */
public class EventExecutorManager {

    private static class EventExecutorManagerHolder {
        private static final EventExecutorManager HOLDER = new EventExecutorManager();
    }

    public static EventExecutorManager getInstance() {
        return EventExecutorManagerHolder.HOLDER;
    }

    private AtomicInteger executorVersion = new AtomicInteger(0);

    private AtomicBoolean initFlag = new AtomicBoolean(false);

    // lock
    private final ReentrantLock lock = new ReentrantLock();

    // 事件执行列表
    private CopyOnWriteArrayList<EventExecutor> eventExecutors;

    /**
     * 当前执行版本
     *
     * @return
     */
    public int getCurrentExecutorVersion() {
        return this.executorVersion.get();
    }

    /**
     * 复位执行版本
     */
    public void resetExecutorVersion() {
        this.executorVersion.set(0);
    }

    /**
     * 是否已经初始化完成
     *
     * @return
     */
    public boolean hasInited() {
        return initFlag.get();
    }

//TODO

    public void clearInReadyRunningQueue() {
        lock.lock();
        try {
            for (EventExecutor eventExecutor : eventExecutors) {
                eventExecutor.clearInReadyRunningQueue();
            }
            EventInConsumerRepository.getInstance().removeAll();
        } finally {
            lock.unlock();
        }
    }

}
