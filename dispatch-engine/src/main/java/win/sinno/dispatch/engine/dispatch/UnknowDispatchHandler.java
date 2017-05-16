package win.sinno.dispatch.engine.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * unknoe dispatch handler
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/16 14:38
 */
public class UnknowDispatchHandler implements DispatchHandler {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    /**
     * 任务执行
     *
     * @param dispatchParam
     * @return
     * @throws Exception
     */
    @Override
    public DispatchResult invoke(DispatchParam dispatchParam) throws Exception {

        LOG.info("unknow dispatch handler:{}", dispatchParam);

        return DispatchResult.FAIL2DISCARD;
    }
}
