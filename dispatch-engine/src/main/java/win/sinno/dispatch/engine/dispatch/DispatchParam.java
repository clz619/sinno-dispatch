package win.sinno.dispatch.engine.dispatch;

import java.util.HashMap;
import java.util.Map;

/**
 * task diapatch param
 * <p>
 * {@link win.sinno.dispatch.api.DispatchTaskEntity} -> parameter
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 15:14.
 */
public class DispatchParam {

    private Map<String, Object> properties = new HashMap<>();

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public String getBizParameter() {
        return (String) this.getProperty("parameter");
    }

    public Integer getRetryTime() {
        return (Integer) this.getProperty("retryTime");
    }

    public Long getTaskId() {
        return (Long) this.getProperty("id");
    }

    public Integer getTaskNode() {
        return (Integer) this.getProperty("node");
    }

    public String getTraceId() {
        return (String) this.getProperty("traceId");
    }

    public String getHandlerGroup() {
        return (String) this.getProperty("handlerGroup");
    }

}
