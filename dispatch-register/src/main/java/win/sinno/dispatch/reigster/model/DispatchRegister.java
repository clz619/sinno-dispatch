package win.sinno.dispatch.reigster.model;

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

    private Date addTs;

    private Date updateTs;

    private String handlerGroup;

    private String hostName;

    private long registerTime;

    private String nodes;

    private String registerVersion;

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

    public String getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(String handlerGroup) {
        this.handlerGroup = handlerGroup;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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
}
