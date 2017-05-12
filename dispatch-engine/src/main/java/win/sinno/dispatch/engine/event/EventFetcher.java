package win.sinno.dispatch.engine.event;

import org.apache.commons.collections4.CollectionUtils;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.api.DispatchTaskService;
import win.sinno.dispatch.api.DispatchContext;
import win.sinno.dispatch.engine.ScheduleServer;

import java.util.Collections;
import java.util.List;

/**
 * task -> event fetcher
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:25
 */
public class EventFetcher {

    public static final int TASK_NUM = 100;

    private DispatchTaskService dispatchTaskService;

    public EventFetcher(DispatchTaskService dispatchTaskService) {
        this.dispatchTaskService = dispatchTaskService;
    }

    /**
     * 获取任务实体集合
     *
     * @param nodeList
     * @return
     */
    public List<DispatchTaskEntity> getTask(List<Integer> nodeList) {

        if (CollectionUtils.isEmpty(nodeList)) {
            throw new IllegalArgumentException("nodeList is empty.");
        }

        List<DispatchTaskEntity> dispatchTaskEntities = null;

        // dispatch context
        DispatchContext dispatchContext = new DispatchContext();
        dispatchContext.setHandlerGroup(ScheduleServer.getInstance().getHandlerGroup());
        dispatchContext.setHostName(ScheduleServer.getInstance().getHostname());
        dispatchContext.setRegisterVersion(ScheduleServer.getInstance().getRegisterVersion());
        dispatchContext.setRegisterTime(ScheduleServer.getInstance().getRegisterTime());

        DispatchService dispatchService = (DispatchService) dispatchTaskService;

        //任务实体集合
        dispatchTaskEntities = dispatchService.findDispatchTasksWithLimit(ScheduleServer.getInstance().getHandlerGroup(), nodeList, TASK_NUM, dispatchContext);

        return dispatchTaskEntities == null ? Collections.<DispatchTaskEntity>emptyList() : dispatchTaskEntities;
    }
}
