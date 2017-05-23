package win.sinno.dispatch.engine.event;

import org.apache.commons.collections4.CollectionUtils;
import win.sinno.dispatch.api.DispatchContext;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.api.DispatchTaskService;
import win.sinno.dispatch.engine.server.HandlerServer;

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

    private HandlerServer handlerServer;

    private DispatchTaskService dispatchTaskService;

    public EventFetcher(HandlerServer handlerServer) {
        this.handlerServer = handlerServer;
        this.dispatchTaskService = handlerServer.getDispatchService();
    }

    /**
     * 获取任务实体集合
     *
     * @param nodeList
     * @return
     */
    public List<DispatchTaskEntity> getTask(List<Integer> nodeList, Integer offset, Integer fetchNum) {

        if (CollectionUtils.isEmpty(nodeList)) {
            throw new IllegalArgumentException("nodeList is empty.");
        }

        List<DispatchTaskEntity> dispatchTaskEntities = null;

        // dispatch context
        DispatchContext dispatchContext = new DispatchContext();
        dispatchContext.setHandlerGroup(handlerServer.getHandlerGroup());
        dispatchContext.setHostName(handlerServer.getHostname());
        dispatchContext.setRegisterVersion(handlerServer.getRegisterVersion());
        dispatchContext.setRegisterTime(handlerServer.getRegisterTime());

        DispatchService dispatchService = (DispatchService) dispatchTaskService;

        //任务实体集合
        dispatchTaskEntities = dispatchService.findDispatchTasksWithLimit(handlerServer.getHandlerGroup(), nodeList, offset, fetchNum, dispatchContext);

        return dispatchTaskEntities == null ? Collections.<DispatchTaskEntity>emptyList() : dispatchTaskEntities;
    }
}
