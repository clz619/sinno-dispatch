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

    private HandlerServer handlerServer;

    private EventExecutorAgent eventExecutorAgent;

    private DispatchHandlerConverter dispatchHandlerConverter;

    private EventConsumerRepository eventConsumerRepository;

    private EventConfig eventConfig;

    private EventFilter eventFilter;

    private EventFetcher eventFetcher;

    private final ReentrantLock lock = new ReentrantLock();

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
                LOG.info("handlder server un run.");
                return;
            }

            int queueSize = this.eventQueueTeam.getTaskCount();

            if (queueSize > 200) {
                LOG.warn("event consumer pool queue size:{}>200,to next fetch task...", new Object[]{queueSize});
                return;
            } else {
                LOG.info("event consumer pool queue size:{}", new Object[]{queueSize});
            }

            List<DispatchTaskEntity> dispatchTaskEntities = eventFetcher.getTask(eventConfig.getNodeList());

            if (CollectionUtils.isEmpty(dispatchTaskEntities)) {
                LOG.warn("no task for handler:{} , nodes:{} , registerVersion:{} , registerTime:{}"
                        , new Object[]{eventConfig.getHandler(),
                                eventConfig.getNodeList(),
                                handlerServer.getRegisterVersion(),
                                handlerServer.getRegisterTime()}
                );
                return;
            }


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
                    continue;
                }

                String handler = dispatchTaskEntity.getHandler();

                DispatchHandler dispatchHandler = dispatchHandlerConverter.converter(handler);

                EventConsumer eventConsumer = new EventConsumer(handlerServer, eventExecutorAgent, dispatchTaskEntity, dispatchHandler, handlerServer.getDispatchResultService(), eventConfig.getHandlerIdentifyCode());

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
}
