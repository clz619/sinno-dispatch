package win.sinno.dispatch.engine.agent;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.api.DispatchContext;
import win.sinno.dispatch.api.DispatchTaskService;
import win.sinno.dispatch.engine.server.HandlerServer;
import win.sinno.dispatch.engine.util.VersionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * handler server cluster status agent
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
public class HanlderServerClusterStatusAgent implements IAgent {

    private static final Logger LOG = LoggerFactory.getLogger(HanlderServerClusterStatusAgent.class);

    private HandlerServer handlerServer;

    private ZkNodeAgent zkNodeAgent;

    private String zkRootPath;

    private String currentNodePath;

    private Thread clusterStatusThread;

    public HanlderServerClusterStatusAgent(HandlerServer server) {
        this.handlerServer = server;

        this.zkNodeAgent = handlerServer.getZkNodeAgent();
        this.zkRootPath = handlerServer.getZkRootPath();
        this.currentNodePath = this.zkRootPath + "/" + handlerServer.getHostname();

        clusterStatusThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!handlerServer.isInit()) {
                            LOG.warn("HandlerServer is not init.");
                            continue;
                        }

                        //machine register path 's server
                        List<String> serverList = zkNodeAgent.getChildrenForPath(zkRootPath);
                        if (CollectionUtils.isEmpty(serverList)) {
                            LOG.warn("HandlerServer init ok,but serverList is empty. path:" + zkRootPath);
                            continue;
                        }

                        // 当前注册版本
                        String currentRegisterVersion = VersionGenerator.version(serverList);

                        if (!StringUtils.equals(currentRegisterVersion
                                , handlerServer.getRegisterVersion())) {
                            // 当前最新版本，与本地运行时缓存中的分配器版本不一致，本地运行时版本号

                            // 版本需要更新
                            // 更新 集群版本
                            // 重置，任务调度器
                            handlerServer.reset();
                            if (handlerServer.isRunning()) {
                                Thread.sleep(1000l);
                                if (handlerServer.isRunning()) {
                                    // after wait 1s,still in running,do next time
                                    continue;
                                }
                            }

                            // current register version
                            String registerData = currentRegisterVersion;

                            //更新当前节点远程zk的版本号
                            zkNodeAgent.setDataForPath(currentNodePath, registerData);

                            // check 集群中机器的版本号是否都更新完成
                            boolean hasReady = true;

                            for (String server : serverList) {
                                String otherNodePath = zkRootPath + "/" + server;
                                if (StringUtils.equals(otherNodePath, currentNodePath)) {
                                    // 忽略当前节点
                                    continue;
                                }

                                String otherRegisterData = zkNodeAgent.getDataForPath(otherNodePath);

                                if (!StringUtils.equals(otherRegisterData, registerData)) {
                                    // 只要有一个节点的注册版本 不一致，则不做之后的节点判断，直接返回，等待其他节点更新完成
                                    hasReady = false;
                                    break;
                                }
                            }

                            if (hasReady) {
                                //更新本机注册版本
                                List<String> handlers = new ArrayList<>();
                                Set<String> handlerSet = handlerServer.getHandlerSet();

                                if (handlerSet.size() == 0) {
                                    //handler config is empty
                                    continue;
                                }
                                handlers.addAll(handlerSet);

                                //获取新的节点
                                List<Integer> newNodeList = getNodeList(serverList);

                                // 注册
                                registerContext(currentRegisterVersion, newNodeList);

                                if (CollectionUtils.isEmpty(newNodeList)) {
                                    //有可能节点启动太多，该节点没有分配到虚拟处理节点，返回不启动执行器
                                    continue;
                                }
                                handlerServer.setNodeList(newNodeList);
                                handlerServer.clearAllHandler();
                                for (String handler : handlers) {
                                    handlerServer.addHandler(handler, new ArrayList<>(newNodeList));
                                }
                                handlerServer.setHandlerIdentifyCode(handlers.hashCode());

                                //注册变更
                                LOG.info("###register version changed, handler constructed with new nodelist:{},registerVersion:{},registerTime:{}"
                                        , new Object[]{newNodeList, handlerServer.getRegisterVersion(), handlerServer.getRegisterTime()});

                                //启动执行器
                                handlerServer.startRunning();
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

        clusterStatusThread.setName("HandlerServerClusterStatusAgent-Thread");
        clusterStatusThread.setPriority(Thread.MAX_PRIORITY);
    }

    public void start() {

        clusterStatusThread.start();

        LOG.info("Handler server cluster status agent start...");
    }

    /**
     * 自我分配节点
     *
     * @param serverList
     * @return
     */
    private List<Integer> getNodeList(List<String> serverList) {
        List<Integer> nodeList = new ArrayList<>();

        int index = serverList.indexOf(handlerServer.getHostname());

        if (index == -1) {
            //不包含本机
            return nodeList;
        }
        //散列模式
        int numOfVisualNode = handlerServer.getVirtualNodeNum();

        for (int i = index; i < numOfVisualNode; i += serverList.size()) {
            nodeList.add(i);
        }
        return nodeList;
    }

    /**
     * register DispatchContext into DB
     * <p>
     * context include (current register version and nodeList)
     */
    private void registerContext(String currentRegisterVersion, List<Integer> nodeList) {
        // 注册时间
        long registerTime = System.currentTimeMillis();

        // update register info
        handlerServer.setRegisterInfo(currentRegisterVersion, registerTime);

        DispatchTaskService dispatchTaskService = handlerServer.getDispatchService();

        try {
            DispatchContext context = new DispatchContext();
            context.setHandlerGroup(handlerServer.getHandlerGroup());
            context.setHostName(handlerServer.getHostname());
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
