package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.api.DispatchTaskService;
import win.sinno.dispatch.api.reigster.DispatchContext;

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

    public static final int TASK_NUM = 200;

    private DispatchTaskService dispatchTaskService;

    public EventFetcher(DispatchTaskService dispatchTaskService) {
        this.dispatchTaskService = dispatchTaskService;
    }

    public List<DispatchTaskEntity> getTask(List<Integer> nodeList) {
        List<DispatchTaskEntity> dispatchTaskEntities = null;

        // dispatch context
        DispatchContext dispatchContext = new DispatchContext();

        return dispatchTaskEntities == null ? Collections.<DispatchTaskEntity>emptyList() : dispatchTaskEntities;
    }
}
