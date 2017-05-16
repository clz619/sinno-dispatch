package win.sinno.dispatch.engine.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.server.HandlerServer;

import java.util.List;

/**
 * zk node watcher
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 16:49
 */
public class ZkNodeWatcher implements CuratorWatcher {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch-engine");

    private HandlerServer handlerServer;

    private CuratorFramework curatorFramework;

    public ZkNodeWatcher(HandlerServer handlerServer, CuratorFramework curatorFramework) {
        this.handlerServer = handlerServer;
        this.curatorFramework = curatorFramework;
    }

    @Override
    public void process(WatchedEvent event) throws Exception {
        LOG.warn("node changed," + event);

        if (curatorFramework.getState() != CuratorFrameworkState.STOPPED) {
            if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                List<String> childList = curatorFramework.getChildren().usingWatcher(this).forPath(handlerServer.getZkRootPath());

                LOG.warn("node child changed," + childList);
                handlerServer.reset();

            } else if (event.getType() == Watcher.Event.EventType.None
                    && (event.getState() == Watcher.Event.KeeperState.Disconnected || event
                    .getState() == Watcher.Event.KeeperState.Expired)) {

                LOG.warn("node disconnected or expired, remove handler infos." + event);
                handlerServer.reset();

            }
        }
    }


}
