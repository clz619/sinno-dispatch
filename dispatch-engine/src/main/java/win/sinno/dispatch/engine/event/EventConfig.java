package win.sinno.dispatch.engine.event;

import java.util.List;

/**
 * event config
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:23
 */
public class EventConfig {

    private String handler;

    private List<Integer> nodeList;

    private Integer identifyCode;

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public List<Integer> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Integer> nodeList) {
        this.nodeList = nodeList;
    }

    public Integer getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(Integer identifyCode) {
        this.identifyCode = identifyCode;
    }
}
