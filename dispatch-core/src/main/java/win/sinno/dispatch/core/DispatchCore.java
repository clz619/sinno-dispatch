package win.sinno.dispatch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.DispatchEngine;
import win.sinno.dispatch.engine.constant.ServerProps;
import win.sinno.dispatch.engine.constant.ZkProps;
import win.sinno.dispatch.engine.dispatch.DispatchHandlerConverter;

import java.util.Properties;

/**
 * dispatch launch
 * <p>
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 17:24
 */

public class DispatchCore {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private Properties properties = new Properties();

    private DispatchService dispatchService;

    private DispatchHandlerConverter dispatchHandlerConverter;

    private DispatchEngine dispatchEngine = new DispatchEngine();

    private Integer virtualNodeNum = 8;

    public void setPerFetchSleepTimeMs(int sleepPerFetchTimeMs) {
        properties.setProperty(ServerProps.SERVER_PER_FETCH_SLEEP_TIMEMS, "" + sleepPerFetchTimeMs);
    }

    public void setPerFetchNum(int perFetchNum) {
        properties.setProperty(ServerProps.SERVER_PER_FETCH_NUM, "" + perFetchNum);
    }

    public void setZkAddress(String zkAddress) {
        properties.setProperty(ZkProps.ZK_ADDRESS, zkAddress);
    }

    public void setZkNamespace(String zkNamespace) {
        properties.setProperty(ZkProps.ZK_NAMESPACE, zkNamespace);
    }

    public void setZkSessionTimeoutMs(Integer zkSessionTimeoutMs) {
        properties.setProperty(ZkProps.ZK_SESSION_TIMEOUT, "" + zkSessionTimeoutMs);
    }

    public void setZkConnectionTimeoutMs(Integer zkConnectionTimeoutMs) {
        properties.setProperty(ZkProps.ZK_CONNECTION_TIMEOUT, "" + zkConnectionTimeoutMs);
    }

    public void setHandlerGroup(String handlerGroup) {
        properties.setProperty(ServerProps.HANDLER_GROUP, handlerGroup);
    }

    public void setHandlers(String handlers) {
        properties.setProperty(ServerProps.HANDLERS, handlers);
    }

    public void setCoreSize(Integer coreSize) {
        properties.setProperty(ServerProps.CORE_SIZE, "" + coreSize);
    }

    public void setMaxSize(Integer maxSize) {
        properties.setProperty(ServerProps.MAX_SIZE, "" + maxSize);
    }

    public void setVirtualNodeNum(Integer virtualNodeNum) {
        properties.setProperty(ServerProps.VIRTUAL_NODE_NUM, "" + virtualNodeNum);

        this.virtualNodeNum = virtualNodeNum;
    }

    public void setDivideType(Integer divideType) {
        properties.setProperty(ServerProps.DIVIDE_TYPE, "" + divideType);
    }

    public void setMaxTrytime(Integer maxTrytime) {
        properties.setProperty(ServerProps.MAX_TRYTIME, "" + maxTrytime);
    }

    public void setDispatchService(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
        properties.put(ServerProps.DISPATCH_SERVICE, dispatchService);
    }

    public void setDispatchHandlerConverter(DispatchHandlerConverter dispatchHandlerConverter) {
        this.dispatchHandlerConverter = dispatchHandlerConverter;
        properties.put(ServerProps.DISPATCH_HANDLER_CONVERTER, dispatchHandlerConverter);
    }

    /**
     * 添加properties
     *
     * @param properties
     */
    public void addProperties(Properties properties) {
        if (properties == null) {
            return;
        }

        this.properties.putAll(properties);
    }

    /**
     * @param key
     * @param val
     */
    public void addProperty(String key, Object val) {
        this.properties.put(key, val);
    }

    public void clearProperties() {
        this.properties.clear();
    }

    /**
     * 启动引擎
     */
    public void startEngine() throws Exception {
        LOG.info("start engine props:{}", new Object[]{properties});

        dispatchEngine.start(properties);
    }

    public Long addDispatchTask(DispatchTaskEntity dispatchTaskEntity) {

        if (dispatchTaskEntity == null) {
            return 0l;
        }

        Integer loadbalance = Math.abs(dispatchTaskEntity.getLoadbalance());
        int node = loadbalance % virtualNodeNum;
        dispatchTaskEntity.setNode(node);

        return dispatchService.addDispatchTask(dispatchTaskEntity);
    }


}
