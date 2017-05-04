package win.sinno.dispatch.engine;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * schedule manager
 * 任务调度分配管理器:<br/>
 * 1)轮询zk集群的变化.<br/>
 * 2)等待本机调度任务完成.<br/>
 * 3)等待集群注册版本全部更新完，自己变更注册版本.<br/>
 * 4)自己重新分配执行节点
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 17:12
 */
public class ScheduleManager {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleManager.class);

    private CuratorFramework curatorFramework;

    private String path;

    public ScheduleManager(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
    }

    public void start() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!ScheduleServer.getInstance().isInitOk()) {
                            LOG.warn("schedule server is un inited.");
                            continue;
                        }

                        //machine register path 's server
                        List<String> serverList = curatorFramework.getChildren().forPath(path);

                        if (CollectionUtils.isEmpty(serverList)) {
                            LOG.warn("schedule server init ok,but server list is empty. path:" + path);
                            continue;
                        }

                        Collections.sort(serverList);

                        String servers = StringUtils.join(serverList.toArray(new String[serverList.size()]), "_");
                        String currentRegisterVersion = DigestUtils.md5Hex(servers);

                        if (!StringUtils.equals(currentRegisterVersion
                                , ScheduleServer.getInstance().getRegisterVersion())) {
                            // 集群版本不一致
                            ScheduleServer.getInstance().reset();
                            if (ScheduleServer.getInstance().isInRunning()) {
                                //
                                Thread.sleep(1000l);
                                if (ScheduleServer.getInstance().isInRunning()) {
                                    // after wait 1s,still in running,do next time
                                    continue;
                                }
                            }

                            // refresh zk node
                            String currentNodePath = path + "/" + ScheduleServer.getInstance().getHostname();
                            // current register version
                            String registerData = currentRegisterVersion;

                            curatorFramework.setData().forPath(currentNodePath, registerData.getBytes("utf-8"));

                            // check 集群 机器的注册版本是否更新完
                            boolean hasReady = true;

                            for (String server : serverList) {
                                String remoteNodePath = path + "/" + server;
                                if (StringUtils.equals(remoteNodePath, currentNodePath)) {
                                    // 忽略当前节点
                                    continue;
                                }

                                byte[] bytes = curatorFramework.getData().forPath(remoteNodePath);
                                String remoteRegisterData = new String(bytes, "utf-8");

                                // 需要更新所有节点
                                if (!StringUtils.equals(remoteRegisterData, registerData)) {
                                    hasReady = false;
                                    break;
                                }

                                if (hasReady) {
                                    
                                    //更新本机注册版本
                                }
                            }
                        }

                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    } finally {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
            }
        };

        thread.setName("scheduleManager-Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        LOG.info("schedule manager start...");
    }

}
