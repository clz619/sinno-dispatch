package win.sinno.dispatch.engine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.engine.agent.EventAgent;
import win.sinno.dispatch.engine.agent.HanlderServerClusterStatusAgent;
import win.sinno.dispatch.engine.agent.ZkNodeAgent;
import win.sinno.dispatch.engine.constant.ServerProps;
import win.sinno.dispatch.engine.constant.ZkProps;
import win.sinno.dispatch.engine.dispatch.DispatchResultService;
import win.sinno.dispatch.engine.server.HandlerServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 执行引擎构造器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 11:20.
 */
public class DispatchEngine {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private AtomicBoolean initFlag = new AtomicBoolean(false);

    private HandlerServer handlerServer = new HandlerServer();

    public HandlerServer getHandlerServer() {
        return this.handlerServer;
    }

    public void start(Properties properties) throws Exception {
        initHandlerServer(properties);
        // after init . start agent
        startAgent();
    }

    /**
     * 初始化-处理服务器
     *
     * @param properties
     * @throws Exception
     */
    public void initHandlerServer(Properties properties) throws Exception {

        if (!initFlag.compareAndSet(false, true)) {
            //只初始化一次
            return;
        }

        String zkAddress = properties.getProperty(ZkProps.ZK_ADDRESS);
        String handlers = properties.getProperty(ServerProps.HANDLERS);
        if (StringUtils.isBlank(zkAddress) || StringUtils.isBlank(handlers)) {
            //zk address ,handlers
            throw new IllegalArgumentException("zk address or handlers is empty");
        }

        handlerServer.setZkAddress(zkAddress);

        // 处理器数组
        String[] handlerArray = handlers.split(",");
        // node list
        List<Integer> nodeList = handlerServer.getNodeList();

        List<String> handlerList = new ArrayList<>();
        for (String handler : handlerArray) {
            //每个handler都会对应一个nodeList
            handlerServer.addHandler(handler, nodeList);
            handlerList.add(handler);
        }

        // handler 的标识
        handlerServer.setHandlerIdentifyCode(handlerList.hashCode());

        String zkNamespace = properties.getProperty(ZkProps.ZK_NAMESPACE);

        String zkSessionTimeout = properties.getProperty(ZkProps.ZK_SESSION_TIMEOUT, "60000");

        String zkConnectionTimeout = properties.getProperty(ZkProps.ZK_CONNECTION_TIMEOUT, "60000");

        handlerServer.setZkNamespace(zkNamespace);

        handlerServer.setZkSessionTimeoutMs(Integer.valueOf(zkSessionTimeout));

        handlerServer.setZkConnectionTimeoutMs(Integer.valueOf(zkConnectionTimeout));

        String handlerGroup = properties.getProperty(ServerProps.HANDLER_GROUP, "dispatch");

        // handler server conf
        String virtualNodeNum = properties.getProperty(ServerProps.VIRTUAL_NODE_NUM, "16");

        String divideType = properties.getProperty(ServerProps.DIVIDE_TYPE);

        String coreSize = properties.getProperty(ServerProps.CORE_SIZE, "2");

        String maxSize = properties.getProperty(ServerProps.MAX_SIZE, "5");

        String maxTrytime = properties.getProperty(ServerProps.MAX_TRYTIME, "3");

        // dispatch service
        Object dsObj = properties.get(ServerProps.DISPATCH_SERVICE);

        if (dsObj != null) {
            DispatchService dispatchService = (DispatchService) dsObj;

            handlerServer.setDispatchService(dispatchService);
        }

        handlerServer.setHandlerGroup(handlerGroup);
        handlerServer.setVirtualNodeNum(Integer.valueOf(virtualNodeNum));
        handlerServer.setHandelrCoreSize(Integer.valueOf(coreSize));
        handlerServer.setHandlerMaxSize(Integer.valueOf(maxSize));
        handlerServer.setMaxTryTime(Integer.valueOf(maxTrytime));

        // zk node agent
        handlerServer.setZkNodeAgent(new ZkNodeAgent(handlerServer));
        // handler server cluster status agent
        handlerServer.setHanlderServerClusterStatusAgent(new HanlderServerClusterStatusAgent(handlerServer));
        // event agent
        handlerServer.setEventAgent(new EventAgent(handlerServer));
        // Dispatch result logic
        handlerServer.setDispatchResultService(new DispatchResultService(handlerServer));

        // 初始化参数完成.
        handlerServer.initDone();

        try {
            String sleepPerFetchTimeMs = properties.getProperty(ServerProps.SLEEP_PER_FETCH_TIME_MS, "10000");
            int sleepPerFetchTimeMsInt = Integer.valueOf(sleepPerFetchTimeMs);
            handlerServer.setSleepTimeMsPerFetch(sleepPerFetchTimeMsInt);
        } catch (Exception ignore) {
            // ignore
        }

    }

    /**
     * start
     *
     * @throws Exception
     */
    private void startAgent() throws Exception {

        startZkNodeAgent();

        startClusterStatusAgent();

        startEventAgent();
    }

    private void startZkNodeAgent() throws Exception {
        handlerServer.startZkNodeAgent();
    }

    private void startClusterStatusAgent() throws Exception {
        handlerServer.startHanlderServerClusterStatusAgent();
    }

    private void startEventAgent() throws Exception {
        handlerServer.startEventAgent();
    }

}
