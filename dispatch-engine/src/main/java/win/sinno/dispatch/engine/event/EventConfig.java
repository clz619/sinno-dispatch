package win.sinno.dispatch.engine.event;

import java.util.List;

/**
 * eventConfig
 * <p>
 * 处理器名
 * 处理节点集合
 * 处理器识别码，集群的每一个版本都对应唯一个处理识别码
 * <p>
 * handler,nodeList,handlerIdentifyCode
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:23
 */
public class EventConfig {

    private String handler;

    private List<Integer> nodeList;

    // handler的识别码，每次集群变动，都会更新（即，每个集群的版本，各对应一个handler识别码）,实际就是集群版本号
    private Integer handlerIdentifyCode;

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

    public Integer getHandlerIdentifyCode() {
        return handlerIdentifyCode;
    }

    public void setHandlerIdentifyCode(Integer handlerIdentifyCode) {
        this.handlerIdentifyCode = handlerIdentifyCode;
    }
}
