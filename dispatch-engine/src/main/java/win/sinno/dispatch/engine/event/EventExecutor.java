package win.sinno.dispatch.engine.event;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.ScheduleServer;
import win.sinno.dispatch.engine.repository.EventInConsumerRepository;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 事件执行器
 * <p>
 * 一个handler 一个fetcher 得到一个executor
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 14:36.
 */
public class EventExecutor {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private EventConfig eventConfig;

    private EventFilter eventFilter;

    private EventFetcher eventFetcher;

    private ThreadPoolExecutor eventThreadPool;

    private final ReentrantLock lock = new ReentrantLock();

    public EventExecutor(final EventConfig eventConfig, EventFilter eventFilter, EventFetcher eventFetcher) {
        this.eventConfig = eventConfig;
        this.eventFilter = eventFilter;
        this.eventFetcher = eventFetcher;

        int coreSize = ScheduleServer.getInstance().getHandelrCoreSize();
        int maxSize = ScheduleServer.getInstance().getHandlerMaxSize();

        String handlerName = eventConfig.getHandler();

        final String handlerNameTemp = handlerName;

        // 时间执行线程池
        this.eventThreadPool = new ThreadPoolExecutor(coreSize, maxSize, 10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10000), new ThreadFactory() {
            AtomicInteger index = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                // thread
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName(handlerNameTemp + "#" + index.incrementAndGet());
                return thread;
            }
        });
    }

    public void doWork() {
        if (eventConfig.getIdentifyCode() != ScheduleServer.getInstance().getHandlerIdentityCode()) {
            //集群发送改变，不做处理
            return;
        }

        lock.lock();

        try {
            if (!ScheduleServer.getInstance().canScheduler()) {
                LOG.info("schedule server disable schedule.");
                return;
            }
            List<DispatchTaskEntity> dispatchTaskEntities = eventFetcher.getTask(eventConfig.getNodeList());

            if (CollectionUtils.isEmpty(dispatchTaskEntities)) {
                LOG.warn("no task for handler:{},nodes:{},registerVersion:{},registerTime:{}"
                        , new Object[]{eventConfig.getHandler(),
                                eventConfig.getNodeList(),
                                ScheduleServer.getInstance().getRegisterVersion(),
                                ScheduleServer.getInstance().getRegisterTime()}
                );
                return;
            }
            // dispatch task
            dispatchTasks(dispatchTaskEntities);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param dispatchTaskEntities
     */
    private void dispatchTasks(List<DispatchTaskEntity> dispatchTaskEntities) {
        // 分配任务实体
        for (DispatchTaskEntity dispatchTaskEntity : dispatchTaskEntities) {
            try {
                //使用仓库进行过滤
                if (!eventFilter.isAccept(dispatchTaskEntity.getId())) {
                    //已在仓库,不处理
                    continue;
                }

                EventConsumer eventConsumer = EventConsumerFactory.create(dispatchTaskEntity, eventConfig);

                eventThreadPool.execute(eventConsumer);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                EventInConsumerRepository.getInstance().remove(dispatchTaskEntity.getId());
            }
        }
    }

    public EventConfig getEventConfig() {
        return eventConfig;
    }

    /**
     * 清空当前事件执行器中的任务队列
     */
    public void clearInReadyRunningQueue() {
        lock.lock();
        try {
            this.eventThreadPool.getQueue().clear();
        } finally {
            lock.unlock();
        }
    }
}
