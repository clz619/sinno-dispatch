package win.sinno.dispatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchParam;
import win.sinno.dispatch.engine.dispatch.DispatchResult;

/**
 * demo handler
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/11 15:34
 */
public class DemoHandler implements DispatchHandler {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch-handler");

    /**
     * 任务执行
     *
     * @param dispatchParam
     * @return
     * @throws Exception
     */
    @Override
    public DispatchResult invoke(DispatchParam dispatchParam) throws Exception {

        long taskId = dispatchParam.getTaskId();

        LOG.info("start exec taskId:{}", new Object[]{taskId});

        String params = dispatchParam.getBizParams();

        LOG.info("finish exec taskId:{},params:{}", new Object[]{taskId, params});

        return DispatchResult.SUCCESS;
    }
}
