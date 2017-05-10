package win.sinno.dispatch.engine.event;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.ScheduleServer;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchParam;
import win.sinno.dispatch.engine.dispatch.DispatchResult;
import win.sinno.dispatch.engine.dispatch.DispatchResultManager;
import win.sinno.dispatch.engine.repository.EventInConsumerRepository;
import win.sinno.dispatch.engine.util.UniqueUtils;

/**
 * event consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 15:10.
 */
public class EventConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("disptach");

    /**
     * 任务实体
     */
    private DispatchTaskEntity dispatchTaskEntity;

    /**
     * 任务处理器
     */
    private DispatchHandler dispatchHandler;

    /**
     * 结果处理
     */
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
        if (eventInConsumerRepository.get(dispatchTaskEntity.getId()) == null
                ) {
            //任务不在本地消费库中
            ScheduleServer.getInstance().decrRunningTask();
            return;
        }

        if (executorVersionSnapshot != EventExecutorManager.getInstance().getCurrentExecutorVersion()) {
            //事件执行版本与当前 集群版本不一致，不进行执行
            eventInConsumerRepository.remove(dispatchTaskEntity.getId());
            ScheduleServer.getInstance().decrRunningTask();
            return;
        }

        DispatchResult result = DispatchResult.SUCCESS;

        //Thread context deal
        try {
            EventThreadContext.init();
            String traceId = dispatchTaskEntity.getTraceId();
            if (StringUtils.isBlank(traceId)) {
                //任务创建后的首次执行
                traceId = UniqueUtils.getUniqueId();
                dispatchTaskEntity.setTraceId(traceId);
            }

            EventThreadContext.put("traceId", traceId);
            //分配参数
            DispatchParam dispatchParam = new DispatchParam();
            dispatchParam.setTraceId(traceId);
            dispatchParam.setTaskId(dispatchTaskEntity.getId());
            dispatchParam.setTaskNode(dispatchTaskEntity.getNode());
            dispatchParam.setHandlerGroup(dispatchTaskEntity.getHandlerGroup());
            dispatchParam.setRetryTime(dispatchTaskEntity.getRetryTime());
            //业务参数
            dispatchParam.setBizParam(dispatchTaskEntity.getParameter());

            long start = System.currentTimeMillis();
            result = dispatchHandler.invoke(dispatchParam);
            long end = System.currentTimeMillis();
            // 调用间隔
            long duration = end - start;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

            // 失败重试
            result = DispatchResult.FAIL2RETRY;
        } finally {
            eventInConsumerRepository.remove(dispatchTaskEntity.getId());
            ScheduleServer.getInstance().decrRunningTask();
            dispatchResultManager.afterConsumer(result, dispatchTaskEntity);
            EventThreadContext.destory();
        }

    }

}
