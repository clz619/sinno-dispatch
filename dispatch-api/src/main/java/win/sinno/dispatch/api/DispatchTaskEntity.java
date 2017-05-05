package win.sinno.dispatch.api;

import win.sinno.model.IBaseModel;

import java.util.Date;

/**
 * dispatch task entity
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/28 13:23
 */
public class DispatchTaskEntity implements IBaseModel {

    private static final long serialVersionUID = -4594829449057000837L;

    /**
     * task id
     */
    private Long id;

    private Date addTs;

    private Date updateTs;

    /**
     * biz uiq id
     */
    private String bizUniqueId;
    /**
     * biz parameter
     */
    private String parameter;

    /**
     * task deal trace id
     */
    private String traceId;

    /**
     * handler group
     */
    private String handlerGroup;

    /**
     * handler name . proxy the true handler
     */
    private String handler;

    /**
     * hander node . = loadbalance%numOfVisualNode
     * <p>
     * 本系统，对于node的设置，非常关键，在于
     */
    private Integer node;

    /**
     * load balance
     */
    private Integer loadbalance;

    /**
     * task status.
     * {@link DispatchTaskEntityStatus}
     */
    private Integer status;

    /**
     * next exec time
     */
    private Long nextExecTime;

    /**
     * fail strategy:
     * 1.retry
     * 2.un retry,dicard
     */
    private Integer failStrategy;

    /**
     * retry time
     */
    private Integer retryTime;

    /**
     * remark
     */
    private String remark;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getAddTs() {
        return addTs;
    }

    @Override
    public void setAddTs(Date addTs) {
        this.addTs = addTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public String getBizUniqueId() {
        return bizUniqueId;
    }

    public void setBizUniqueId(String bizUniqueId) {
        this.bizUniqueId = bizUniqueId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(String handlerGroup) {
        this.handlerGroup = handlerGroup;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Integer getNode() {
        return node;
    }

    public void setNode(Integer node) {
        this.node = node;
    }

    public Integer getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(Integer loadbalance) {
        this.loadbalance = loadbalance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getNextExecTime() {
        return nextExecTime;
    }

    public void setNextExecTime(Long nextExecTime) {
        this.nextExecTime = nextExecTime;
    }

    public Integer getFailStrategy() {
        return failStrategy;
    }

    public void setFailStrategy(Integer failStrategy) {
        this.failStrategy = failStrategy;
    }

    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
