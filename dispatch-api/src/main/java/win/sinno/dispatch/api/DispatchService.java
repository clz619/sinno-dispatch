package win.sinno.dispatch.api;

import win.sinno.dispatch.api.reigster.DispatchContext;

import java.util.List;

/**
 * get task with handler group , then dispatch by handler
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 11:58
 */
public interface DispatchService extends DispatchTaskService {


    /**
     * find task with check current register's DispatchContext
     *
     * @param handlerGroup
     * @param nodeList
     * @param limit
     * @param dispatchContext
     * @return
     */
    List<DispatchTaskEntity> findDispatchTasksWithLimit(String handlerGroup, List<Integer> nodeList,
                                                        int limit, DispatchContext dispatchContext);


}
