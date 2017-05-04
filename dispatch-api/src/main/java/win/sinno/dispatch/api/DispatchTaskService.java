package win.sinno.dispatch.api;

import win.sinno.dispatch.api.reigster.DispatchContext;

import java.util.Date;

/**
 * task operate service
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 11:32
 */
public interface DispatchTaskService {


    /**
     * add dispatch task
     *
     * @param dispatchTaskEntity : handlerGroup,handler,loadbalance,nextExecTime can`t null.
     * @return
     */
    long addDispatchTask(DispatchTaskEntity dispatchTaskEntity);

    /**
     * update task status
     *
     * @param taskId
     * @param status
     * @param taskAttribute
     * @return
     */
    boolean updateTaskStatus(long taskId, int status, TaskAttribute taskAttribute);

    /**
     * add retry times and set next execute time
     *
     * @param taskId
     * @param nextExecuteTime
     * @param taskAttribute
     * @return
     */
    boolean addRetryTimesAndExecuteTime(long taskId, Date nextExecuteTime, TaskAttribute taskAttribute);

    /**
     * fail retry.
     * add retry times and set next execute time
     *
     * @param taskId
     * @param nextExecuteTime
     * @param taskAttribute
     * @return
     */
    boolean addRetryTimesByFail(long taskId, Date nextExecuteTime, TaskAttribute taskAttribute);

    /**
     * remove a task
     *
     * @param handlerGroup
     * @param taskAttribute
     * @return
     */
    boolean removeDispatchTask(String handlerGroup, TaskAttribute taskAttribute);

    // ------------ register dispatch context

    /**
     * register dispatch context
     *
     * @param dispatchContext
     * @return
     */
    boolean registerContext(DispatchContext dispatchContext);

}
