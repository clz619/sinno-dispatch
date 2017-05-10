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

    // 业务参数
    public static final String BIZ_PARAM = "bizParam";

    // 尝试次数
    public static final String RETRY_TIME = "retryTime";

    /**
     * 任务id
     */
    public static final String TASK_ID = "taskId";

    /**
     * 任务节点
     */
    public static final String NODE = "node";

    /**
     * 跟踪id
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 处理组
     */
    public static final String HANDLER_GROUP = "handlerGroup";


    private Map<String, Object> properties = new HashMap<>();

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public String getBizParams() {
        Object obj = this.getProperty(BIZ_PARAM);
        if (obj == null) {
            return null;
        }
        return (String) obj;
    }

    public void setBizParam(String param) {
        addProperty(BIZ_PARAM, param);
    }

    public Integer getRetryTime() {
        Object obj = this.getProperty(RETRY_TIME);
        if (obj == null) {
            return null;
        }
        return (Integer) obj;
    }

    public void setRetryTime(Integer retryTime) {
        addProperty(RETRY_TIME, retryTime);
    }

    public Long getTaskId() {
        Object obj = this.getProperty(TASK_ID);
        if (obj == null) {
            return null;
        }
        return (Long) obj;
    }

    public void setTaskId(Long taskId) {
        addProperty(TASK_ID, taskId);
    }

    public Integer getTaskNode() {
        Object obj = this.getProperty(NODE);
        if (obj == null) {
            return null;
        }
        return (Integer) obj;
    }

    public void setTaskNode(Integer node) {
        addProperty(NODE, node);
    }

    public String getTraceId() {
        Object obj = this.getProperty(TRACE_ID);
        if (obj == null) {
            return null;
        }
        return (String) obj;
    }

    public void setTraceId(String traceId) {
        addProperty(TRACE_ID, traceId);
    }

    public String getHandlerGroup() {
        Object obj = this.getProperty(HANDLER_GROUP);
        if (obj == null) {
            return null;
        }
        return (String) obj;
    }

    public void setHandlerGroup(String handlerGroup) {
        addProperty(HANDLER_GROUP, handlerGroup);
    }

}
