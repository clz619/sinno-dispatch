package win.sinno.dispatch.engine.event;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchParam;
import win.sinno.dispatch.engine.dispatch.DispatchResult;
import win.sinno.dispatch.engine.dispatch.DispatchResultService;
import win.sinno.dispatch.engine.repository.EventConsumerRepository;
import win.sinno.dispatch.engine.server.HandlerServer;
import win.sinno.dispatch.engine.util.UniqueUtils;

/**
 * event consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 15:10.
 */
public class EventConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private HandlerServer handlerServer;

    private EventExecutor eventExecutor;

    private EventExecutorAgent eventExecutorManager;

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
    private DispatchResultService dispatchResultService;

    /**
     * 事件消费仓库
     */
    private EventConsumerRepository eventConsumerRepository;

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
    public EventConsumer(HandlerServer handlerServer, EventExecutor eventExecutor, EventExecutorAgent eventExecutorManager, DispatchTaskEntity dispatchTaskEntity, DispatchHandler dispatchHandler,
                         DispatchResultService dispatchResultManager, int executorVersionSnapshot) {
        this.handlerServer = handlerServer;
        this.eventExecutor = eventExecutor;
        this.eventExecutorManager = eventExecutorManager;
        this.dispatchTaskEntity = dispatchTaskEntity;
        this.dispatchHandler = dispatchHandler;
        this.dispatchResultService = dispatchResultManager;
        this.eventConsumerRepository = handlerServer.getEventConsumerRepository();

        this.executorVersionSnapshot = executorVersionSnapshot;
    }

    @Override
    public void run() {
        // incr running task
        handlerServer.incrRunningCount();

        if (eventConsumerRepository.get(dispatchTaskEntity.getId()) == null) {
            //任务不在本地消费库中
            handlerServer.decrRunningCount();
            return;
        }

        if (executorVersionSnapshot != eventExecutorManager.getCurrentExecHandlerIdentityCode()) {
            //事件执行版本与当前 集群版本不一致，不进行执行
            eventConsumerRepository.remove(dispatchTaskEntity.getId());
            handlerServer.decrRunningCount();
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

            DispatchParam dispatchParam = new DispatchParam();
            dispatchParam.setTraceId(traceId);
            dispatchParam.setTaskId(dispatchTaskEntity.getId());
            dispatchParam.setTaskNode(dispatchTaskEntity.getNode());
            dispatchParam.setHandlerGroup(dispatchTaskEntity.getHandlerGroup());
            dispatchParam.setRetryTime(dispatchTaskEntity.getRetryTime());
            dispatchParam.setBizParam(dispatchTaskEntity.getParameter());

            result = dispatchHandler.invoke(dispatchParam);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

            result = DispatchResult.FAIL2RETRY;
        } finally {
            eventConsumerRepository.remove(dispatchTaskEntity.getId());
            handlerServer.decrRunningCount();
            dispatchResultService.afterConsumer(result, dispatchTaskEntity);

            // dispatch result
            if (result == DispatchResult.SUCCESS) {
                eventExecutor.incrSuccess(1);
            } else {
                eventExecutor.incrFail(1);
            }

            EventThreadContext.destory();
        }

    }

}
