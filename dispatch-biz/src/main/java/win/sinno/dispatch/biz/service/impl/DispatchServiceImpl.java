package win.sinno.dispatch.biz.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import win.sinno.dispatch.api.*;
import win.sinno.dispatch.biz.dao.DispatchTaskEntityDao;
import win.sinno.dispatch.register.DispatchRegisterManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 机器注册，任务管理服务
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/11 10:46
 */
@Component("bizDispatchService")
public class DispatchServiceImpl implements DispatchService {

    /**
     * task dao
     */
    @Autowired
    private DispatchTaskEntityDao dispatchTaskEntityDao;

    /**
     * register
     */
    @Autowired
    private DispatchRegisterManager dispatchRegisterManager;

    /**
     * find task with check current register's DispatchContext
     *
     * @param handlerGroup
     * @param nodes
     * @param limit
     * @param dispatchContext
     * @return
     */
    @Override
    public List<DispatchTaskEntity> findDispatchTasksWithLimit(String handlerGroup, List<Integer> nodes, int limit, DispatchContext dispatchContext) {

        if (StringUtils.isBlank(handlerGroup)
                || CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }

        List<win.sinno.dispatch.biz.model.DispatchTaskEntity> dispatchTaskEntities = dispatchTaskEntityDao.selectWithLimit(handlerGroup, nodes, limit);

        if (CollectionUtils.isEmpty(dispatchTaskEntities)) {
            return Collections.emptyList();
        }

        List<DispatchTaskEntity> dispatchTaskEntityList = new ArrayList<>();

        for (win.sinno.dispatch.biz.model.DispatchTaskEntity dispatchTaskEntity : dispatchTaskEntities) {
            DispatchTaskEntity dte = new DispatchTaskEntity();
            BeanUtils.copyProperties(dispatchTaskEntity, dte);
            dispatchTaskEntityList.add(dte);
        }

        return dispatchTaskEntityList;
    }

    /**
     * add dispatch task
     *
     * @param dispatchTaskEntity : handlerGroup,handler,loadbalance,nextExecTime can`t null.
     * @return
     */
    @Override
    public long addDispatchTask(DispatchTaskEntity dispatchTaskEntity) {

        win.sinno.dispatch.biz.model.DispatchTaskEntity dte = new win.sinno.dispatch.biz.model.DispatchTaskEntity();
        BeanUtils.copyProperties(dispatchTaskEntity, dte);

        return dispatchTaskEntityDao.insert(dte);
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
        win.sinno.dispatch.biz.model.DispatchTaskEntity dispatchTaskEntity = new win.sinno.dispatch.biz.model.DispatchTaskEntity();
        dispatchTaskEntity.setId(taskId);
        dispatchTaskEntity.setStatus(status);

        return dispatchTaskEntityDao.updateById(dispatchTaskEntity) > 0;
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
        win.sinno.dispatch.biz.model.DispatchTaskEntity dispatchTaskEntity = new win.sinno.dispatch.biz.model.DispatchTaskEntity();
        dispatchTaskEntity.setId(taskId);
        dispatchTaskEntity.setNextExecTime(nextExecuteTime.getTime());

        return dispatchTaskEntityDao.updateById(dispatchTaskEntity) > 0;
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
        win.sinno.dispatch.biz.model.DispatchTaskEntity dispatchTaskEntity = new win.sinno.dispatch.biz.model.DispatchTaskEntity();
        dispatchTaskEntity.setId(taskId);
        dispatchTaskEntity.setNextExecTime(nextExecuteTime.getTime());

        return dispatchTaskEntityDao.updateById(dispatchTaskEntity) > 0;
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
        // 取消任务
        if (StringUtils.isBlank(handlerGroup) || taskAttribute == null) {
            return false;
        }

        //直接将状态改为fail 失败
        int status = DispatchTaskEntityStatus.FAIL.getCode();

        if (taskAttribute.getTaskId() > 0) {
            return dispatchTaskEntityDao.updateStatusByIdAndHandlerGroup(taskAttribute.getTaskId(), handlerGroup, status) > 0;
        }

        if (StringUtils.isNotBlank(taskAttribute.getBizUniqueId())) {
            return dispatchTaskEntityDao.updateStatusByBizIdAndHandlerGroup(taskAttribute.getBizUniqueId(), handlerGroup, status) > 0;
        }

        return false;
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
                || dispatchContext.getRegisterTime() < 1000
                ) {
            return false;
        }

        return dispatchRegisterManager.register(dispatchContext);
    }

}
