package win.sinno.dispatch.engine.event;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.concurrent.earthworm.DataQueueCenter;
import win.sinno.concurrent.earthworm.DataQueueTeam;
import win.sinno.concurrent.earthworm.custom.AbsDataTeamConf;
import win.sinno.concurrent.earthworm.custom.IDataHandler;
import win.sinno.concurrent.earthworm.custom.IDataTeamConf;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchHandlerConverter;
import win.sinno.dispatch.engine.repository.EventConsumerRepository;
import win.sinno.dispatch.engine.server.HandlerServer;

import java.util.List;
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

    private final ReentrantLock lock = new ReentrantLock();

    private HandlerServer handlerServer;

    private EventExecutorAgent eventExecutorAgent;

    private DispatchHandlerConverter dispatchHandlerConverter;

    private EventConsumerRepository eventConsumerRepository;

    private EventConfig eventConfig;

    private EventFilter eventFilter;

    private EventFetcher eventFetcher;

    private EventFetchStat eventExecutorStat;

    private DataQueueCenter dataQueueCenter;

    private IDataTeamConf<EventConsumer> eventDataTeamConf;

    private IDataHandler<EventConsumer> eventDataHandler;

    private DataQueueTeam<EventConsumer> eventQueueTeam;

    public EventExecutor(HandlerServer handlerServer, EventExecutorAgent eventExecutorAgent, EventConfig eventConfig, EventFilter eventFilter, EventFetcher eventFetcher) {
        this.handlerServer = handlerServer;
        this.eventExecutorAgent = eventExecutorAgent;
        this.dispatchHandlerConverter = handlerServer.getDispatchHandlerConverter();
        this.eventConsumerRepository = handlerServer.getEventConsumerRepository();

        this.eventConfig = eventConfig;
        this.eventFilter = eventFilter;
        this.eventFetcher = eventFetcher;

        this.eventExecutorStat = new EventFetchStat();
        this.eventExecutorStat.setDefaultFetchNum(handlerServer.getPerFetchNum());
        this.eventExecutorStat.setNowFetchNum(handlerServer.getPerFetchNum());
        this.eventExecutorStat.setDefaultFetchPerTs(handlerServer.getPerFetchSleepTimeMs());
        this.eventExecutorStat.setNowFetchPerTs(handlerServer.getPerFetchSleepTimeMs());

        String handlerName = eventConfig.getHandler();
        final String handlerNameTemp = handlerName;

        int coreSize = handlerServer.getHandelrCoreSize();
        final int maxSize = handlerServer.getHandlerMaxSize();

        this.dataQueueCenter = handlerServer.getDataQueueCenter();

        this.eventDataHandler = new IDataHandler<EventConsumer>() {
            @Override
            public void handler(EventConsumer eventConsumer) throws InterruptedException {
                eventConsumer.run();
            }
        };

        this.eventDataTeamConf = new AbsDataTeamConf<EventConsumer>() {

            @Override
            public String getTeamName(EventConsumer eventConsumer) {
                return "EventExecutor#" + handlerNameTemp;
            }

            @Override
            public IDataHandler<EventConsumer> getDataHandler() {
                return eventDataHandler;
            }

            @Override
            public int getWorkerNum() {
                return maxSize;
            }
        };

        this.eventQueueTeam = this.dataQueueCenter.createDataQueueTeam(handlerNameTemp, eventDataTeamConf);

    }

    private boolean isDoNow() {
        long lastFetchTs = eventExecutorStat.getLastFetchTs();
        long nowFetchPerTs = eventExecutorStat.getNowFetchPerTs();

        if ((lastFetchTs + nowFetchPerTs) > System.currentTimeMillis()) {
            LOG.info("not do now !!");
            return false;
        }

        return true;
    }

    public void doWork() {
        if (eventConfig.getHandlerIdentifyCode() != handlerServer.getHandlerIdentityCode()) {
            //集群发送改变，不做处理
            return;
        }

        // FIXME
        // 若处理不过来，会导致线程堵塞
        // 应该改成获取的数据，丢入处理池，处理池根据现有的处理事件量，再进行事件获取
        lock.lock();

        try {
            if (!handlerServer.isCanRunning()) {
                LOG.info("handler server un run.");
                return;
            }

            //FIXME 处理速度 小于 获取速度，会取出重复的事件，需要进行控制
            // 优化事件处理速度，保证获取的事件在获取间隔内能处理完
            // 优化 获取逻辑，记录上次获取的最后一个事件，从这个事件之后进行获取，但也有可能导致异常重试的事件获取异常
            int queueSize = this.eventQueueTeam.getTaskCount();

            if (queueSize > 50) {
                LOG.warn("event consumer pool queue size:{} > 50 , to next fetch task...", new Object[]{queueSize});
                return;
            } else {
                LOG.info("event consumer pool queue size:{}", new Object[]{queueSize});
            }

            if (!isDoNow()) {
                return;
            }
            eventExecutorStat.setLastFetchTs(System.currentTimeMillis());

            LOG.info("event executor handler:{} , nodelist:{} , registerVersion:{} , registerTime:{} , stat:{}",
                    new Object[]{eventConfig.getHandler(), eventConfig.getNodeList()
                            , handlerServer.getRegisterVersion()
                            , handlerServer.getRegisterTime(), eventExecutorStat});

            List<DispatchTaskEntity> dispatchTaskEntities = eventFetcher.getTask(eventConfig.getNodeList(), 0, eventExecutorStat.getNowFetchNum());
            eventExecutorStat.setLastFetchNum(dispatchTaskEntities.size());

            if (CollectionUtils.isEmpty(dispatchTaskEntities)) {
                eventExecutorStat.incrNowFetchPerTs();
                return;
            } else {
//                if (dispatchTaskEntities.size() == eventExecutorStat.getNowFetchNum()) {
//                    eventExecutorStat.setNowFetchPerTs(eventExecutorStat.getMinFetchPerTs());
//                } else {
                eventExecutorStat.decrNowFetchPerTs();
//                }
            }

            eventExecutorStat.incrFetch(dispatchTaskEntities.size());

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

        for (DispatchTaskEntity dispatchTaskEntity : dispatchTaskEntities) {

            try {
                // repo check if not contain to add
                if (!eventFilter.isAccept(dispatchTaskEntity.getId())) {
                    eventExecutorStat.incrFilter(1);
                    continue;
                }

                String handler = dispatchTaskEntity.getHandler();

                DispatchHandler dispatchHandler = dispatchHandlerConverter.converter(handler);

                EventConsumer eventConsumer = new EventConsumer(handlerServer, this, eventExecutorAgent, dispatchTaskEntity, dispatchHandler, handlerServer.getDispatchResultService(), eventConfig.getHandlerIdentifyCode());

                //queue team add task
                eventQueueTeam.addTask(eventConsumer);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                // repo remove
                eventConsumerRepository.remove(dispatchTaskEntity.getId());
            }
        }
    }

    /**
     * 清空当前事件执行器中的任务队列
     */
    public void clearInReadyRunningQueue() {
        lock.lock();
        try {
            this.eventQueueTeam.clearTask();
        } finally {
            lock.unlock();
        }
    }

    /**
     * incr success
     *
     * @param num
     * @return
     */
    public long incrSuccess(long num) {
        return eventExecutorStat.incrSuccess(num);
    }

    /**
     * incr fail
     *
     * @param num
     * @return
     */
    public long incrFail(long num) {
        return eventExecutorStat.incrFail(num);
    }
}
