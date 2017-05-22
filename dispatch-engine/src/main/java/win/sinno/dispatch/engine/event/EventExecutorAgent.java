package win.sinno.dispatch.engine.event;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.repository.EventConsumerRepository;
import win.sinno.dispatch.engine.server.HandlerServer;

import java.util.List;
import java.util.concurrent.*;
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
public class EventExecutorAgent {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private final ReentrantLock lock = new ReentrantLock();

    private HandlerServer handlerServer;

    private EventConsumerRepository eventConsumerRepository;

    private AtomicInteger currentExecHandlerIdentityCode = new AtomicInteger(0);

    private AtomicBoolean isInit = new AtomicBoolean(false);

    private List<EventExecutor> eventExecutors = new CopyOnWriteArrayList<>();

    private static ThreadPoolExecutor eventExecutorThreadPool;

    public EventExecutorAgent(HandlerServer handlerServer) {

        this.handlerServer = handlerServer;

        this.eventConsumerRepository = handlerServer.getEventConsumerRepository();

        eventExecutorThreadPool = new ThreadPoolExecutor(4, 32, 1000l, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5000)
                , new ThreadFactory() {

            AtomicInteger index = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("EventExecutorAgent#" + index.incrementAndGet());
                return thread;
            }
        });

    }

    /**
     * 是否已经初始化完成
     *
     * @return
     */
    public boolean isInit() {
        return isInit.get();
    }

    /**
     * 当前执行版本
     *
     * @return
     */
    public int getCurrentExecHandlerIdentityCode() {
        return this.currentExecHandlerIdentityCode.get();
    }

    /**
     * 复位执行版本
     */
    public void resetExecHandlerIdentityCode() {
        this.currentExecHandlerIdentityCode.set(0);
    }

    /**
     * init
     *
     * @param eventConfigs
     */
    public void init(List<EventConfig> eventConfigs) {
        if (CollectionUtils.isEmpty(eventConfigs)
                || CollectionUtils.isEmpty(eventConfigs.get(0).getNodeList())) {
            return;
        }

        LOG.info("event executor agent init . event config:{}", new Object[]{eventConfigs});

        lock.lock();
        try {
            EventConfig eventConfig = eventConfigs.get(0);

            eventExecutors.clear();

            // 设置当前handler 的 标识码
            currentExecHandlerIdentityCode.set(eventConfig.getHandlerIdentifyCode());

            // init event executor
            EventExecutor eventExecutor = EventExecutorFactory.create(handlerServer, this, eventConfig);

            eventExecutors.add(eventExecutor);

            // 初始化完成
            isInit.set(true);
        } finally {
            lock.unlock();
        }
    }

    public List<EventExecutor> getEventExecutors() {
        lock.lock();
        try {
            return eventExecutors;
        } finally {
            lock.unlock();
        }

    }

    public void execute() {
        List<EventExecutor> eventExecutors = getEventExecutors();
        for (final EventExecutor eventExecutor : eventExecutors) {
            try {
                eventExecutorThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        eventExecutor.doWork();
                    }
                });
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public void clearInReadyRunningQueue() {
        lock.lock();

        try {
            for (EventExecutor eventExecutor : eventExecutors) {
                eventExecutor.clearInReadyRunningQueue();
            }
            //remove all from repository
            eventConsumerRepository.removeAll();
        } finally {
            lock.unlock();
        }
    }

}
