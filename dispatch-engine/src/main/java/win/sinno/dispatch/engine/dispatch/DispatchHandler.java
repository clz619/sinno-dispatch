package win.sinno.dispatch.engine.dispatch;

/**
 * dispatch handler
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 15:32.
 */
public interface DispatchHandler {

    /**
     * 任务执行
     *
     * @param dispatchParam
     * @return
     * @throws Exception
     */
    DispatchResult invoke(DispatchParam dispatchParam) throws Exception;

}
