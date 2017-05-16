package win.sinno.dispatch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.engine.DispatchEngine;
import win.sinno.dispatch.engine.constant.ServerProps;
import win.sinno.dispatch.engine.constant.ZkProps;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * dispatch launch
 * <p>
 * TODO start
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 17:24
 */

public class DispatchCoreAgent {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private Properties properties = new Properties();

    private DispatchService dispatchService;

    private DispatchEngine dispatchEngine = new DispatchEngine();

    private int intervalMs = 30 * 1000;

    private AtomicBoolean runFlag = new AtomicBoolean(false);


    public void setIntervalMs(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    public void setZkAddress(String zkAddress) {
        properties.put(ZkProps.ZK_ADDRESS, zkAddress);
    }

    public void setZkNamespace(String zkNamespace) {
        properties.put(ZkProps.ZK_NAMESPACE, zkNamespace);
    }

    public void setZkSessionTimeoutMs(Integer zkSessionTimeoutMs) {
        properties.put(ZkProps.ZK_SESSION_TIMEOUT, zkSessionTimeoutMs);
    }

    public void setZkConnectionTimeoutMs(Integer zkConnectionTimeoutMs) {
        properties.put(ZkProps.ZK_CONNECTION_TIMEOUT, zkConnectionTimeoutMs);
    }

    public void setHandlerGroup(String handlerGroup) {
        properties.put(ServerProps.HANDLER_GROUP, handlerGroup);
    }

    public void setHandlers(String handlers) {
        properties.put(ServerProps.HANDLERS, handlers);
    }

    public void setCoreSize(Integer coreSize) {
        properties.put(ServerProps.CORE_SIZE, coreSize);
    }

    public void setMaxSize(Integer maxSize) {
        properties.put(ServerProps.MAX_SIZE, maxSize);
    }

    public void setVirtualNodeNum(Integer virtualNodeNum) {
        properties.put(ServerProps.VIRTUAL_NODE_NUM, virtualNodeNum);
    }

    public void setDivideType(Integer divideType) {
        properties.put(ServerProps.DIVIDE_TYPE, divideType);
    }

    public void setMaxTrytime(Integer maxTrytime) {
        properties.put(ServerProps.MAX_TRYTIME, maxTrytime);
    }

    public void setDispatchService(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
        properties.put(ServerProps.DISPATCH_SERVICE, dispatchService);
    }

    /**
     * 启动引擎
     */
    public void startEngine() throws Exception {
        dispatchEngine.start(properties);
    }



}
