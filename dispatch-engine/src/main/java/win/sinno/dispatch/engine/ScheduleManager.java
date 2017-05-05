package win.sinno.dispatch.engine;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchTaskService;
import win.sinno.dispatch.api.reigster.DispatchContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
                            // 当前最新版本，与本地运行时缓存中的分配器版本不一致，本地运行时版本号
                            // 版本需要更新
                            // 更新 集群版本
                            // 重置，任务调度器
                            ScheduleServer.getInstance().reset();
                            if (ScheduleServer.getInstance().isInRunning()) {
                                Thread.sleep(1000l);
                                if (ScheduleServer.getInstance().isInRunning()) {
                                    // after wait 1s,still in running,do next time
                                    continue;
                                }
                            }

                            // 当前 zk node的path
                            String currentNodePath = path + "/" + ScheduleServer.getInstance().getHostname();
                            // current register version
                            String registerData = currentRegisterVersion;

                            //更新当前节点远程zk的版本号
                            curatorFramework.setData().forPath(currentNodePath, registerData.getBytes("utf-8"));

                            // check 集群中机器的版本号是否都更新完成
                            boolean hasReady = true;

                            for (String server : serverList) {
                                String otherNodePath = path + "/" + server;
                                if (StringUtils.equals(otherNodePath, currentNodePath)) {
                                    // 忽略当前节点
                                    continue;
                                }

                                byte[] bytes = curatorFramework.getData().forPath(otherNodePath);
                                String otherRegisterData = new String(bytes, "utf-8");

                                if (!StringUtils.equals(otherRegisterData, registerData)) {
                                    // 只要有一个节点的注册版本 不一致，则不做之后的节点判断，直接返回，等待其他节点更新完成
                                    hasReady = false;
                                    break;
                                }
                            }

                            if (hasReady) {
                                //更新本机注册版本
                                List<String> handlers = new ArrayList<>();
                                Set<String> handlerSet = ScheduleServer.getInstance().getHandlers();

                                if (handlerSet.size() == 0) {
                                    //handler config is empty
                                    continue;
                                }
                                handlers.addAll(handlerSet);

                                //获取新的节点
                                List<Integer> newNodeList = getNodeList(serverList);

                                registerContext(currentRegisterVersion, newNodeList);

                                if (CollectionUtils.isEmpty(newNodeList)) {
                                    //有可能节点启动太多，该节点没有分配到虚拟处理节点，返回不启动执行器
                                    continue;
                                }
                                ScheduleServer.getInstance().setNodeList(newNodeList);
                                ScheduleServer.getInstance().clearAllHandler();
                                for (String handler : handlers) {
                                    ScheduleServer.getInstance().addHandler(handler, new ArrayList<>(newNodeList));
                                }
                                ScheduleServer.getInstance().setHandlerIdentifyCode(handlers.hashCode());

                                //注册变更
                                LOG.info("###register version changed, handler constructed with new nodelist:{},registerVersion:{},registerTime:{}"
                                        , new Object[]{newNodeList, ScheduleServer.getInstance().getRegisterVersion(), ScheduleServer.getInstance().getRegisterTime()});

                                //启动执行器
                                ScheduleServer.getInstance().startScheduler();
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

    /**
     * 自我分配节点
     *
     * @param serverList
     * @return
     */
    private List<Integer> getNodeList(List<String> serverList) {
        List<Integer> nodeList = new ArrayList<>();

        int index = serverList.indexOf(ScheduleServer.getInstance().getHostname());

        if (index == -1) {
            //不包含本机
            return nodeList;
        }
        //散列模式
        int numOfVisualNode = ScheduleServer.getInstance().getVirtualNodeNum();

        for (int i = index; i < numOfVisualNode; i += serverList.size()) {
            nodeList.add(i);
        }
        return nodeList;
    }

    /**
     * node register context
     */
    private void registerContext(String currentRegisterVersion, List<Integer> nodeList) {
        // 注册时间
        long registerTime = System.currentTimeMillis() / 1000;

        // update register info
        ScheduleServer.getInstance().setRegister(currentRegisterVersion, registerTime);

        // TODO 真实任务分配接口 注入
        DispatchTaskService dispatchTaskService = null;

        try {
            DispatchContext context = new DispatchContext();
            context.setHandlerGroup(ScheduleServer.getInstance().getHandlerGroup());
            context.setHostName(ScheduleServer.getInstance().getHostname());
            context.setRegisterVersion(currentRegisterVersion);
            context.setRegisterTime(registerTime);
            context.setNodeList(nodeList);

            // register context
            boolean flag = dispatchTaskService.registerContext(context);
            if (!flag) {
                Thread.sleep(100);
                dispatchTaskService.registerContext(context);
            }

            Thread.sleep(50);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
