package win.sinno.dispatch.engine.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.ScheduleServer;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchResultManager;
import win.sinno.dispatch.engine.repository.EventInConsumerRepository;

/**
 * event consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 15:10.
 */
public class EventConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("disptach");

    private DispatchTaskEntity dispatchTaskEntity;

    private DispatchHandler dispatchHandler;

    private DispatchResultManager dispatchResultManager;

    /**
     * 事件消费仓库
     */
    private EventInConsumerRepository eventInConsumerRepository;

    /**
     * 执行版本快照
     */
    private int executorVersionSnapshot;

    /**
     * 事件消费
     *
     * @param dispatchTaskEntity
     * @param dispatchHandler
     * @param dispatchResultManager
     * @param executorVersionSnapshot
     */
    public EventConsumer(DispatchTaskEntity dispatchTaskEntity, DispatchHandler dispatchHandler,
                         DispatchResultManager dispatchResultManager, int executorVersionSnapshot) {
        this.dispatchTaskEntity = dispatchTaskEntity;
        this.dispatchHandler = dispatchHandler;
        this.dispatchResultManager = dispatchResultManager;
        this.executorVersionSnapshot = executorVersionSnapshot;
        this.eventInConsumerRepository = EventInConsumerRepository.getInstance();
    }

    @Override
    public void run() {
        // incr running task
        ScheduleServer.getInstance().incrRunningTask();

        // TODO
        //        if (eventInConsumerRepository.get(dispatchTaskEntity.getId()) == null
        //                ||executorVersionSnapshot!=EventEx) {

    }

}
