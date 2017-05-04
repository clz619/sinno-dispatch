package win.sinno.dispatch.engine.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.ScheduleServer;

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

    private CuratorFramework curatorFramework;

    private String path;

    public ZkNodeWatcher(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
    }

    @Override
    public void process(WatchedEvent event) throws Exception {
        LOG.warn("node changed," + event);

        if (curatorFramework.getState() != CuratorFrameworkState.STOPPED) {
            if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                List<String> childList = curatorFramework.getChildren().usingWatcher(this).forPath(path);

                LOG.warn("node child changed," + childList);
                ScheduleServer.getInstance().reset();

            } else if (event.getType() == Watcher.Event.EventType.None
                    && (event.getState() == Watcher.Event.KeeperState.Disconnected || event
                    .getState() == Watcher.Event.KeeperState.Expired)) {

                LOG.warn("node disconnected or expired, remove handler infos." + event);
                ScheduleServer.getInstance().reset();

            }
        }
    }


}
