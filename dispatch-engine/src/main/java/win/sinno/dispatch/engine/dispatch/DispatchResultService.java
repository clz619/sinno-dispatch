package win.sinno.dispatch.engine.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.api.DispatchTaskEntityStatus;
import win.sinno.dispatch.api.DispatchTaskService;
import win.sinno.dispatch.api.TaskAttribute;
import win.sinno.dispatch.engine.server.HandlerServer;

import java.util.Calendar;

/**
 * dispatch result logic
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 15:34.
 */
public class DispatchResultService {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    // handler server
    private HandlerServer handlerServer;

    // dispatch task service
    private DispatchTaskService dispatchTaskService;

    public DispatchResultService(HandlerServer handlerServer) {
        this.handlerServer = handlerServer;
        this.dispatchTaskService = handlerServer.getDispatchService();
    }

    /**
     * after consumer
     *
     * @param dispatchResult
     * @param dispatchTaskEntity
     */
    public void afterConsumer(DispatchResult dispatchResult, DispatchTaskEntity dispatchTaskEntity) {

        if (dispatchResult == null || dispatchTaskEntity == null) {
            return;
        }

        TaskAttribute taskAttribute = new TaskAttribute();
        taskAttribute.setHostname(handlerServer.getHostname());
        taskAttribute.setTraceId(dispatchTaskEntity.getTraceId());
        taskAttribute.setNode(dispatchTaskEntity.getNode());

        boolean flag = false;

        switch (dispatchResult) {

            case SUCCESS:

                flag = dispatchTaskService.updateTaskStatus(dispatchTaskEntity.getId()
                        , DispatchTaskEntityStatus.SUCCESS.getCode(), taskAttribute);

                if (!flag) {
                    // dispatch task
                    LOG.warn("task:({}) execute success,update status failed.", new Object[]{dispatchTaskEntity});
                }
                break;

            case FAIL2RETRY:

                if (dispatchTaskEntity.getRetryTime() < handlerServer.getMaxTryTime()) {
                    //
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MINUTE, dispatchTaskEntity.getRetryTime() + 1);//多重试一次，多延迟1分钟后执行

                    flag = dispatchTaskService.addRetryTimesByFail(
                            dispatchTaskEntity.getId(),
                            c.getTime(), taskAttribute);

                    if (!flag) {
                        LOG.warn("task:({}) executr failed,update retry time failed", new Object[]{dispatchTaskEntity});
                    }

                } else {
                    LOG.info("");

                    flag = dispatchTaskService.updateTaskStatus(dispatchTaskEntity.getId()
                            , DispatchTaskEntityStatus.FAIL.getCode(), taskAttribute);

                    if (!flag) {
                        LOG.warn("task:({}) executr failed,update status failed", new Object[]{dispatchTaskEntity});
                    }
                }
                break;

            case NEXT:

                //.. TODO 执行下一个
                break;

            default:

                // FAIL2DISCARD
                flag = dispatchTaskService.updateTaskStatus(dispatchTaskEntity.getId()
                        , DispatchTaskEntityStatus.FAIL.getCode(), taskAttribute);

                if (!flag) {
                    LOG.warn("task:({}) executr discard,update status failed", new Object[]{dispatchTaskEntity});
                }

                break;
        }
    }
}
