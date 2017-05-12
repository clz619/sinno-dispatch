package win.sinno.dispatch.register.model;

import win.sinno.model.IBaseModel;

import java.util.Date;

/**
 * dispatch register model
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 14:03
 */
public class DispatchRegister implements IBaseModel {

    private static final long serialVersionUID = -7640358514357584001L;

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    /**
     * 处理器群组
     */
    private String handlerGroup;

    /**
     * 机器名
     */
    private String hostname;

    /**
     * 注册事件
     */
    private long registerTime;

    /**
     * 负责节点 如： 1,3,5,7
     */
    private String nodes;

    /**
     * 注册版本，集群(serverA,serverB,...,serverN).hashCode()
     */
    private String registerVersion;

    /**
     * 当前机器节点在版本，如首次进入集群为1，之后集群改变（有新机器加入，或机器退出），每次版本变更，version+1
     */
    private long version;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Override
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public Date getGmtModified() {
        return gmtModified;
    }

    @Override
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(String handlerGroup) {
        this.handlerGroup = handlerGroup;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getRegisterVersion() {
        return registerVersion;
    }

    public void setRegisterVersion(String registerVersion) {
        this.registerVersion = registerVersion;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DispatchRegister{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", handlerGroup='" + handlerGroup + '\'' +
                ", hostname='" + hostname + '\'' +
                ", registerTime=" + registerTime +
                ", nodes='" + nodes + '\'' +
                ", registerVersion='" + registerVersion + '\'' +
                ", version=" + version +
                '}';
    }
}
