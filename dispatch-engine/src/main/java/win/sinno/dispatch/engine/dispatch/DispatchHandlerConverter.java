package win.sinno.dispatch.engine.dispatch;

/**
 * 获取 DispatchHandler 实例
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/16 14:22
 */
public interface DispatchHandlerConverter {

    /**
     * converter
     *
     * @param handler
     * @return DispatchHandler
     */
    DispatchHandler converter(String handler);
}
