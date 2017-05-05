package win.sinno.dispatch.engine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.constant.ScheduleProps;
import win.sinno.dispatch.engine.constant.ZkProps;
import win.sinno.dispatch.engine.zk.ZkNodeAgent;

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
public class ScheduleManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private ZkNodeAgent zkNodeAgent = new ZkNodeAgent();

    /**
     * 每次轮询任务的时间间隔,默认30ms
     */
    private int sleepInterval = 30 * 1000;

    private Thread eventStarterThread;

    private AtomicBoolean initFlag = new AtomicBoolean(false);

    /**
     * @param sleepTimeMsPerFetch
     */
    public ScheduleManagerFactory(int sleepTimeMsPerFetch) {
        if (sleepTimeMsPerFetch > 10) {
            this.sleepInterval = sleepTimeMsPerFetch;
        }
    }

    public void initScheduleServer(Properties properties) throws Exception {
        if (!initFlag.compareAndSet(false, true)) {
            //只初始化一次
            return;
        }

        String zkAddress = properties.getProperty(ZkProps.ZK_ADDRESS);
        String handlers = properties.getProperty(ScheduleProps.HANDLERS);
        if (StringUtils.isBlank(zkAddress) || StringUtils.isBlank(handlers)) {
            //zk address ,handlers
            throw new IllegalArgumentException("zk address or handlers is empty");
        }

        ScheduleServer.getInstance().setZkAddress(zkAddress);
        // 处理器数组
        String[] handlerArray = handlers.split(",");
        // node list
        List<Integer> nodeList = ScheduleServer.getInstance().getNodeList();

        List<String> handlerList = new ArrayList<>();
        for (String handler : handlerArray) {
            //每个handler都会对应一个nodeList
            ScheduleServer.getInstance().addHandler(handler, nodeList);
            handlerList.add(handler);
        }
        // handler 的标识
        ScheduleServer.getInstance().setHandlerIdentifyCode(handlerList.hashCode());

        // root path
        String zkRootPath = properties.getProperty(ZkProps.ZK_ROOT_PATH, "dispatch");

        String zkSessionTimeout = properties.getProperty(ZkProps.ZK_SESSION_TIMEOUT, "60000");

        String zkConnectionTimeout = properties.getProperty(ZkProps.ZK_CONNECTION_TIMEOUT, "60000");

        String zkUsername = properties.getProperty(ZkProps.ZK_USERNAME);

        String zkPassword = properties.getProperty(ZkProps.ZK_PASSWORD);

        String handlerGroup = properties.getProperty(ScheduleProps.HANDLER_GROUP, "dispatch");

        String virtualNodeNum = properties.getProperty(ScheduleProps.VIRTUAL_NODE_NUM, "16");

        String divideType = properties.getProperty(ScheduleProps.DIVIDE_TYPE);

        String coreSize = properties.getProperty(ScheduleProps.CORE_SIZE);

        String maxSize = properties.getProperty(ScheduleProps.MAX_SIZE);

        ScheduleServer.getInstance().setZkRootPath(zkRootPath);
        ScheduleServer.getInstance().setZkSessionTimeoutMs(Integer.valueOf(zkSessionTimeout));
        ScheduleServer.getInstance().setZkConnectionTimeoutMs(Integer.valueOf(zkConnectionTimeout));

        ScheduleServer.getInstance().setVirtualNodeNum(Integer.valueOf(virtualNodeNum));
        ScheduleServer.getInstance().setHandelrCoreSize(Integer.valueOf(coreSize));
        ScheduleServer.getInstance().setHandlerMaxSize(Integer.valueOf(maxSize));

        // 初始化参数完成.
        ScheduleServer.getInstance().initOk();

        // 启动
        start();
    }

    private void start() throws Exception {
        startZk();

        startSchedule();
    }

    private void startZk() throws Exception {
        zkNodeAgent.start();
    }

    private void startSchedule() {

        eventStarterThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (ScheduleServer.getInstance().canScheduler()) {
                            // 集群分配完成
                            // event 处理

                        }
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        };

        eventStarterThread.setDaemon(true);
        eventStarterThread.setName("EventExecutorSchedule");
        eventStarterThread.start();
    }

}
