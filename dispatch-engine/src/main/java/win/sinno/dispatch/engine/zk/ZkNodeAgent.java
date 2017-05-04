package win.sinno.dispatch.engine.zk;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.ScheduleManager;
import win.sinno.dispatch.engine.ScheduleServer;

/**
 * ak node agent
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:34
 */
public class ZkNodeAgent {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch-engine");

    private String namespace = "sinno";

    private String path = "/dispatch";

    private String zkAddress;

    private int sessionTimeoutMs = 30000;

    private int connTimeoutMs = 15000;

    private CuratorFramework curatorFramework;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnTimeoutMs() {
        return connTimeoutMs;
    }

    public void setConnTimeoutMs(int connTimeoutMs) {
        this.connTimeoutMs = connTimeoutMs;
    }

    public void start() throws Exception {
        LOG.info("###start connecting to zk .");

        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .namespace(namespace)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connTimeoutMs)
                .retryPolicy(new RetryNTimes(10000, 3000))
                .build();

        this.curatorFramework.start();

        try {

            LOG.info("curator client connecting...");
            //阻塞 直到连接
            this.curatorFramework.blockUntilConnected();

            LOG.info("curator client connected... enjoy it.");

        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        createRootNode();
        final CuratorWatcher watcher = new ZkNodeWatcher(curatorFramework, path);
        curatorFramework.getChildren().usingWatcher(watcher).forPath(path);

        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.LOST
                        || newState == ConnectionState.SUSPENDED) {
                    LOG.warn("######zksession timeout or connection loss, begining to reConnectAndRegister..."
                            + newState);
                    ScheduleServer.getInstance().reset();
                    // reconn register
                    reConnectionAndRegister(watcher, path);
                }
            }
        });

        register();
        startScheduleManager();

    }

    private void createRootNode() throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if (stat == null) {
            String createPath = curatorFramework.create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, "0".getBytes("utf-8"));

            if (StringUtils.isNotBlank(createPath)) {
                LOG.info("zk root path created :" + path);
            } else {
                LOG.error("zk root create failed :" + path);
                throw new Exception("zk root create failed :" + path);
            }

        } else {
            LOG.info("zk root path already exist :" + path);
        }
    }

    private void register() {
        String nodePath = path + "/" + ScheduleServer.getInstance().getHostname();

        try {
            Stat stat = curatorFramework.checkExists().forPath(nodePath);

            if (stat != null) {
                curatorFramework.delete().forPath(nodePath);
            }

            String registerData = ScheduleServer.getInstance().getRegisterVersion();
            String resultPath = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(nodePath, registerData.getBytes("utf-8"));

            if (StringUtils.isNotBlank(resultPath)) {
                //不为空
                LOG.warn("schedule server register successed. result path:" + resultPath);
            } else {
                LOG.warn("schedule server node register failed. node path:" + nodePath);
            }

        } catch (Exception e) {
            throw new RuntimeException("schedule server node register to zk failed.");
        }
    }

    private void reConnectionAndRegister(final CuratorWatcher watcher, final String path) {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);

                        LOG.info("start reconnection to zk.");
                        if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                            LOG.info("zk reconnected success.");
                            createRootNode();

                            curatorFramework.getChildren().usingWatcher(watcher).forPath(path);

                            register();

                            break;
                        }

                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage(), e);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e1) {
                            LOG.error(e1.getMessage(), e1);
                        }
                    }
                }
            }
        }.start();
    }

    private void startScheduleManager() {
        ScheduleManager scheduleManager = new ScheduleManager(curatorFramework, path);
        scheduleManager.start();
    }
}
