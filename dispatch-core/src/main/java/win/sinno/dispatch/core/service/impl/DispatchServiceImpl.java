package win.sinno.dispatch.core.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import win.sinno.common.util.JsonUtil;
import win.sinno.dispatch.api.*;
import win.sinno.dispatch.engine.ScheduleServer;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * dispatch service
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/11 14:37
 */
@Component("coreDispatchService")
public class DispatchServiceImpl implements DispatchService {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    /**
     * dispatch biz service
     */
    @Autowired
    @Qualifier("bizDispatchService")
    private DispatchService dispatchBizService;

    public DispatchServiceImpl(DispatchService dispatchBizService) {
        this.dispatchBizService = dispatchBizService;
    }

    /**
     * add dispatch task
     *
     * @param dispatchTaskEntity : handlerGroup,handler,loadbalance,nextExecTime can`t null.
     * @return
     */
    @Override
    public long addDispatchTask(DispatchTaskEntity dispatchTaskEntity) {
        if (dispatchTaskEntity == null
                || StringUtils.isBlank(dispatchTaskEntity.getHandlerGroup())
                || StringUtils.isBlank(dispatchTaskEntity.getHandler())) {
            return 0;
        }

        DispatchTaskEntity bizTask = new DispatchTaskEntity();
        bizTask.setHandlerGroup(dispatchTaskEntity.getHandlerGroup());
        bizTask.setHandler(dispatchTaskEntity.getHandler());

        int loadbalance = Math.abs(dispatchTaskEntity.getLoadbalance());
        bizTask.setNode(loadbalance % ScheduleServer.getInstance().getVirtualNodeNum());

        bizTask.setRetryTime(0);
        bizTask.setStatus(DispatchTaskEntityStatus.NEW.getCode());
        bizTask.setTraceId(dispatchTaskEntity.getTraceId());

        if (dispatchTaskEntity.getNextExecTime() == null) {
            bizTask.setNextExecTime(System.currentTimeMillis());
        } else {
            bizTask.setNextExecTime(dispatchTaskEntity.getNextExecTime());
        }

        if (StringUtils.isBlank(dispatchTaskEntity.getParameter())) {
            bizTask.setParameter(JsonUtil.toJson(new HashMap<String, String>()));
        } else {
            bizTask.setParameter(dispatchTaskEntity.getParameter());
        }

        bizTask.setBizUniqueId(dispatchTaskEntity.getBizUniqueId());

        try {
            long ret = dispatchBizService.addDispatchTask(bizTask);
            return ret;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * update task status
     *
     * @param taskId
     * @param status
     * @param taskAttribute
     * @return
     */
    @Override
    public boolean updateTaskStatus(long taskId, int status, TaskAttribute taskAttribute) {
        if (taskId < 1 || taskAttribute == null || taskAttribute.getNode() == null) {
            return false;
        }
        return dispatchBizService.updateTaskStatus(taskId, status, taskAttribute);
    }

    /**
     * add retry times and set next execute time
     *
     * @param taskId
     * @param nextExecuteTime
     * @param taskAttribute
     * @return
     */
    @Override
    public boolean addRetryTimesAndExecuteTime(long taskId, Date nextExecuteTime, TaskAttribute taskAttribute) {
        if (taskId < 1 || nextExecuteTime == null
                || taskAttribute == null || taskAttribute.getNode() == null) {
            return false;
        }
        return dispatchBizService.addRetryTimesAndExecuteTime(taskId, nextExecuteTime, taskAttribute);
    }

    /**
     * fail retry.
     * add retry times and set next execute time
     *
     * @param taskId
     * @param nextExecuteTime
     * @param taskAttribute
     * @return
     */
    @Override
    public boolean addRetryTimesByFail(long taskId, Date nextExecuteTime, TaskAttribute taskAttribute) {
        if (taskId < 1 || nextExecuteTime == null
                || taskAttribute == null || taskAttribute.getNode() == null) {
            return false;
        }

        return dispatchBizService.addRetryTimesByFail(taskId, nextExecuteTime, taskAttribute);

    }

    /**
     * remove a task
     *
     * @param handlerGroup
     * @param taskAttribute
     * @return
     */
    @Override
    public boolean removeDispatchTask(String handlerGroup, TaskAttribute taskAttribute) {
        if (StringUtils.isBlank(handlerGroup)
                || taskAttribute == null
                || taskAttribute.getNode() == null) {
            return false;
        }

        return dispatchBizService.removeDispatchTask(handlerGroup, taskAttribute);
    }

    /**
     * register dispatch context
     *
     * @param dispatchContext
     * @return
     */
    @Override
    public boolean registerContext(DispatchContext dispatchContext) {
        if (dispatchContext == null
                || StringUtils.isBlank(dispatchContext.getHandlerGroup())
                || StringUtils.isBlank(dispatchContext.getHostName())
                || StringUtils.isBlank(dispatchContext.getRegisterVersion())
                || dispatchContext.getRegisterTime() < 1000) {
            return false;
        }

        return dispatchBizService.registerContext(dispatchContext);
    }

    /**
     * find task with check current register's DispatchContext
     *
     * @param handlerGroup
     * @param nodeList
     * @param limit
     * @param dispatchContext
     * @return
     */
    @Override
    public List<DispatchTaskEntity> findDispatchTasksWithLimit(String handlerGroup, List<Integer> nodeList, int limit, DispatchContext dispatchContext) {
        if (StringUtils.isBlank(handlerGroup)
                || CollectionUtils.isEmpty(nodeList) || limit <= 0
                ) {
            return Collections.emptyList();
        }

        if (dispatchContext == null
                || StringUtils.isBlank(dispatchContext.getHandlerGroup())
                || StringUtils.isBlank(dispatchContext.getRegisterVersion())) {
            return Collections.emptyList();
        }

        return dispatchBizService.findDispatchTasksWithLimit(handlerGroup, nodeList, limit, dispatchContext);
    }
}
