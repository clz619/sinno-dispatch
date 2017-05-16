package win.sinno.dispatch.engine.server;

import win.sinno.common.util.NetworkUtil;
import win.sinno.dispatch.engine.constant.ServerProps;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务器运行时数据
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 18:10
 */
public class HandlerServerRunningData {

    // 机器名
    private String hostname;

    /**
     * 注册集群 版本号
     * {@link win.sinno.dispatch.engine.util.VersionGenerator} version generator
     * List<String> server.-> md5(servers)
     */
    private volatile String registerVersion = ServerProps.DEFAULT_REGISTER_VERSION;

    // 注册时间
    private volatile long registerTime = 100;

    // 是否初始化
    private AtomicBoolean isInit = new AtomicBoolean(false);

    // 是否可执行.(集群状态更新完成状态下)
    private AtomicBoolean isCanRunning = new AtomicBoolean(false);

    // 注册，等待集群更新状态，集群更新，集群运行，停止
    private int status;

    // 处理器 识别码
    private AtomicInteger handlerIdentifyCode = new AtomicInteger(0);

    private Set<String> handlerSet = new CopyOnWriteArraySet<>();

    private List<Integer> nodeList = new CopyOnWriteArrayList<>();

    private Map<String, List<Integer>> handlerRefNodeMap = new ConcurrentHashMap<>();

    // 正在运行数量
    private AtomicInteger runningCount = new AtomicInteger(0);
    // 成功数量
    private AtomicInteger successCount = new AtomicInteger(0);
    // 失败数量
    private AtomicInteger failCount = new AtomicInteger(0);

    {
        try {
            hostname = NetworkUtil.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String getHostname() {
        return hostname;
    }

    public boolean isInit() {
        return isInit.get();
    }

    public void initDone() {
        isInit.set(true);
    }

    /**
     * 是否正在运行
     *
     * @return boolean
     */
    public boolean isRunning() {
        return runningCount.get() > 0;
    }

    public boolean isCanRunning() {
        return this.isCanRunning.get();
    }

    public void startRunning() {
        this.isCanRunning.set(true);
    }

    public void stopRunning() {
        this.isCanRunning.set(false);
    }

    public String getRegisterVersion() {
        return registerVersion;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterInfo(String registerVersion, Long registerTime) {
        this.registerVersion = registerVersion;
        this.registerTime = registerTime;
    }

    public int getRunningCount() {
        return runningCount.get();
    }

    /**
     * 增加运行数量
     */
    public void incrRunningCount() {
        runningCount.incrementAndGet();
    }

    /**
     * 减少运行数量
     */
    public void decrRunningCount() {
        int num = runningCount.decrementAndGet();
        if (num < 0) {
            runningCount.set(0);
        }
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public void incrSuccessCount() {
        successCount.incrementAndGet();
    }

    public int getFailCount() {
        return failCount.get();
    }

    public void incrFailCount() {
        failCount.incrementAndGet();
    }

    public int getHandlerIdentifyCode() {
        return this.handlerIdentifyCode.get();
    }

    public void setHandlerIdentifyCode(int handlerIdentifyCode) {
        this.handlerIdentifyCode.set(handlerIdentifyCode);
    }

    public void addHandler(String handler, List<Integer> nodeList) {
        handlerRefNodeMap.put(handler, nodeList);
        handlerSet.add(handler);
    }

    public Set<String> getHandlerSet() {
        return handlerSet;
    }

    public Map<String, List<Integer>> getHandlerRefNodeMap() {
        return this.handlerRefNodeMap;
    }

    public void clearAllHandler() {
        this.handlerRefNodeMap.clear();
    }

    public void setNodeList(List<Integer> nodeList) {
        // set node list
        this.nodeList.clear();
        this.nodeList.addAll(nodeList);
    }

    public List<Integer> getNodeList() {
        return this.nodeList;
    }

    @Override
    public String toString() {
        return "HandlerServerRunningData{" +
                "hostname='" + hostname + '\'' +
                ", registerVersion='" + registerVersion + '\'' +
                ", registerTime=" + registerTime +
                ", isInit=" + isInit +
                ", isCanRunning=" + isCanRunning +
                ", status=" + status +
                ", handlerIdentifyCode=" + handlerIdentifyCode +
                ", handlerSet=" + handlerSet +
                ", nodeList=" + nodeList +
                ", handlerRefNodeMap=" + handlerRefNodeMap +
                ", runningCount=" + runningCount +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                '}';
    }
}
