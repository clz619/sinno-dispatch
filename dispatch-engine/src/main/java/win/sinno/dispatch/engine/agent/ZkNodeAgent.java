package win.sinno.dispatch.engine.agent;

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
import win.sinno.dispatch.engine.server.HandlerServer;
import win.sinno.dispatch.engine.server.HandlerServerZkConf;
import win.sinno.dispatch.engine.zk.ZkNodeWatcher;

import java.util.List;

/**
 * ak node agent
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:34
 */
public class ZkNodeAgent implements IAgent {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch-engine");

    private HandlerServerZkConf handlerServerZkConf;

    private HandlerServer handlerServer;

    private CuratorFramework curatorFramework;

    public ZkNodeAgent(HandlerServer handlerServer) {
        this.handlerServer = handlerServer;
        this.handlerServerZkConf = handlerServer.getHandlerServerZkConf();
    }

    public List<String> getChildrenForPath(String path) throws Exception {
        return curatorFramework.getChildren().forPath(path);
    }

    public void setDataForPath(String path, String data) throws Exception {
        curatorFramework.setData().forPath(path, data.getBytes("utf-8"));
    }

    public String getDataForPath(String path) throws Exception {
        String data = null;
        byte[] bytes = curatorFramework.getData().forPath(path);

        if (bytes != null) {
            data = new String(bytes, "utf-8");
        }

        return data;
    }

    public void start() throws Exception {
        LOG.info("###start connecting to zk .");

        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(handlerServerZkConf.getZkAddress())
                .namespace(handlerServerZkConf.getZkNamespace())
                .sessionTimeoutMs(handlerServerZkConf.getZkSessionTimeoutMs())
                .connectionTimeoutMs(handlerServerZkConf.getZkConnectionTimeoutMs())
                .retryPolicy(new RetryNTimes(10000, 3000))
                .build();

        this.curatorFramework.start();

        try {
            LOG.info("curator client connecting...");

            this.curatorFramework.blockUntilConnected();

            LOG.info("curator client connected... enjoy it.");
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        // 创建根节点
        createRootNode();

        register();

        final CuratorWatcher watcher = new ZkNodeWatcher(handlerServer, curatorFramework);

        curatorFramework.getChildren().usingWatcher(watcher).forPath(handlerServerZkConf.getZkRootPath());

        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.LOST
                        || newState == ConnectionState.SUSPENDED) {
                    LOG.warn("######zkSession timeout or connection loss, begining to reConnectAndRegister..."
                            + newState);
                    handlerServer.reset();
                    // reconnection register
                    reConnectionAndRegister(watcher, handlerServerZkConf.getZkRootPath());
                }
            }
        });


    }

    private void createRootNode() throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(handlerServerZkConf.getZkRootPath());
        if (stat == null) {
            String createPath = curatorFramework.create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(handlerServerZkConf.getZkRootPath());

            if (StringUtils.isNotBlank(createPath)) {
                LOG.info("zk root rootPath created :" + handlerServerZkConf.getZkRootPath());
            } else {
                LOG.error("zk root create failed :" + handlerServerZkConf.getZkRootPath());
                throw new Exception("zk root create failed :" + handlerServerZkConf.getZkRootPath());
            }

        } else {
            LOG.info("zk root rootPath already exist :" + handlerServerZkConf.getZkRootPath());
        }
    }

    private void register() {
        String nodePath = handlerServerZkConf.getZkRootPath() + "/" + handlerServer.getHostname();

        try {
            Stat stat = curatorFramework.checkExists().forPath(nodePath);
            if (stat != null) {
                curatorFramework.delete().forPath(nodePath);
            }

            // first 版本为 0
            String registerData = handlerServer.getRegisterVersion();

            String resultPath = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(nodePath, registerData.getBytes("utf-8"));

            if (StringUtils.isNotBlank(resultPath)) {
                //不为空
                LOG.warn("handler server register successed. result rootPath:" + resultPath);
            } else {
                LOG.warn("handler server node register failed. node rootPath:" + nodePath);
            }

        } catch (Exception e) {
            throw new RuntimeException("handler server node register to zk failed.");
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
                            //重新注册完成 break 退出线程
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

    @Override
    public String toString() {
        return "ZkNodeAgent{" +
                "handlerServerZkConf=" + handlerServerZkConf +
                '}';
    }
}
