package win.sinno.dispatch.engine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.engine.constant.ScheduleProps;
import win.sinno.dispatch.engine.constant.ZkProps;
import win.sinno.dispatch.engine.event.EventConfig;
import win.sinno.dispatch.engine.event.EventConfigUtil;
import win.sinno.dispatch.engine.event.EventExecutorManager;
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

        String coreSize = properties.getProperty(ScheduleProps.CORE_SIZE, "2");

        String maxSize = properties.getProperty(ScheduleProps.MAX_SIZE, "5");

        String maxTryTime = properties.getProperty(ScheduleProps.MAX_TRY_TIME, "3");

        Object dsObj = properties.get(ScheduleProps.DISPATCH_SERVICE);

        if (dsObj != null) {
            DispatchService dispatchService = (DispatchService) dsObj;
            // 分配服务
            ScheduleServer.getInstance().setDispatchService(dispatchService);
        }

        ScheduleServer.getInstance().setZkRootPath(zkRootPath);
        ScheduleServer.getInstance().setZkSessionTimeoutMs(Integer.valueOf(zkSessionTimeout));
        ScheduleServer.getInstance().setZkConnectionTimeoutMs(Integer.valueOf(zkConnectionTimeout));

        ScheduleServer.getInstance().setVirtualNodeNum(Integer.valueOf(virtualNodeNum));
        ScheduleServer.getInstance().setHandelrCoreSize(Integer.valueOf(coreSize));
        ScheduleServer.getInstance().setHandlerMaxSize(Integer.valueOf(maxSize));
        ScheduleServer.getInstance().setMaxTryTime(Integer.valueOf(maxTryTime));

        // 初始化参数完成.
        ScheduleServer.getInstance().initOk();

        // 启动
        start();
    }

    /**
     * start
     *
     * @throws Exception
     */
    private void start() throws Exception {
        startZk();

        startSchedule();
    }

    private void startZk() throws Exception {
        zkNodeAgent.setZkAddress(ScheduleServer.getInstance().getZkAddress());
        zkNodeAgent.setConnTimeoutMs(ScheduleServer.getInstance().getZkConnectionTimeoutMs());
        zkNodeAgent.setSessionTimeoutMs(ScheduleServer.getInstance().getZkSessionTimeoutMs());
        zkNodeAgent.setNamespace(ScheduleServer.getInstance().getZkNamespace());
        zkNodeAgent.setPath(ScheduleServer.getInstance().getZkRootPath());
        zkNodeAgent.start();
    }

    private void startSchedule() {

        eventStarterThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (ScheduleServer.getInstance().canScheduler()) {
                            // 集群分配完成<->才可以进行调度
                            // event 处理
                            if (!EventExecutorManager.getInstance().hasInited()) {
                                List<EventConfig> eventConfigs = EventConfigUtil.syncEventConfig();
                                //初始化完 event executor
                                EventExecutorManager.getInstance().init(eventConfigs);
                            }
                            // 丢线程执行事件
                            EventExecutorScheduler.getInstance().execute();
                        }

                        if (EventExecutorManager.getInstance().hasInited()) {
                            // 轮询时间 - FIXME 此处 时间，无法保证每次轮询出来的数据能够在 间隔时间内完成，需要修改事件处理器的事件获取方式，
                            Thread.sleep(sleepInterval);
                        } else {
                            Thread.sleep(1000);
                            continue;
                        }

                        if (EventExecutorManager.getInstance().getCurrentExecutorVersion() != ScheduleServer.getInstance().getHandlerIdentityCode()) {
                            Thread.sleep(3000);

                            // 更新事件处理器
                            List<EventConfig> eventConfigs = EventConfigUtil.syncEventConfig();
                            EventExecutorManager.getInstance().init(eventConfigs);

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
