package win.sinno.dispatch.api;

import java.io.Serializable;

/**
 * task attribute
 * DispatchTaskEntity's shadow with deal machine info
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 11:24
 */
public class TaskAttribute implements Serializable {
    private static final long serialVersionUID = -7795626649882964962L;

    /**
     * deal machine 's hostname
     */
    private String hostname;

    /**
     * task trace id
     */
    private String traceId;

    /**
     * task id {@link DispatchTaskEntity}.id
     */
    private long taskId;

    /**
     * {@link DispatchTaskEntity}.bizUniqueId
     */
    private String bizUniqueId;

    /**
     * {@link DispatchTaskEntity}.node
     */
    private Integer node;

    public TaskAttribute() {
    }

    public TaskAttribute(String hostname, String traceId) {
        this.hostname = hostname;
        this.traceId = traceId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getBizUniqueId() {
        return bizUniqueId;
    }

    public void setBizUniqueId(String bizUniqueId) {
        this.bizUniqueId = bizUniqueId;
    }

    public Integer getNode() {
        return node;
    }

    public void setNode(Integer node) {
        this.node = node;
    }

}
