package win.sinno.dispatch.api;

import java.io.Serializable;
import java.util.List;

/**
 * dispatch context
 * <p>
 * 注册上下文对象
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 11:27
 */
public class DispatchContext implements Serializable {

    private static final long serialVersionUID = -5642201570335187939L;

    private String handlerGroup;

    private String registerVersion;

    private List<Integer> nodeList;

    private long registerTime;

    private String hostName;

    public String getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(String handlerGroup) {
        this.handlerGroup = handlerGroup;
    }

    public String getRegisterVersion() {
        return registerVersion;
    }

    public void setRegisterVersion(String registerVersion) {
        this.registerVersion = registerVersion;
    }

    public List<Integer> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Integer> nodeList) {
        this.nodeList = nodeList;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return "DispatchContext{" +
                "handlerGroup='" + handlerGroup + '\'' +
                ", registerVersion='" + registerVersion + '\'' +
                ", nodeList=" + nodeList +
                ", registerTime=" + registerTime +
                ", hostName='" + hostName + '\'' +
                '}';
    }
}
