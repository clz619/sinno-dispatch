package win.sinno.dispatch.engine;

import win.sinno.common.util.NetworkUtil;
import win.sinno.dispatch.api.DispatchService;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * schedule server
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:50
 */
public class ScheduleServer {

    private ScheduleServer() {
    }

    private static class ScheduleServerHolder {
        private static final ScheduleServer HOLDER = new ScheduleServer();
    }

    public static ScheduleServer getInstance() {
        return ScheduleServerHolder.HOLDER;
    }

    // ---------- zk begin
    private String zkAddress;

    private String zkNamespace;

    // rootPath=/handlerGroup
    private String zkRootPath;

    private int zkSessionTimeoutMs = 30000;

    private int zkConnectionTimeoutMs = 15000;

    // node
    private int virtualNodeNum = 8;

    private CopyOnWriteArraySet<String> handlers = new CopyOnWriteArraySet<>();

    private CopyOnWriteArrayList<Integer> nodeList = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap<String, List<Integer>> handlerMap = new ConcurrentHashMap<>();

    private String handlerGroup;

    // handler coresize
    private int handelrCoreSize = 2;

    // handler maxsize
    private int handlerMaxSize = 5;

    private int maxTryTime = 3;

    private AtomicInteger handlerIdentifyCode = new AtomicInteger(0);

    // md5(servername0_..._servernamen)
    private volatile String registerVersion = "0";

    // register time
    private volatile long registerTime = 100;

    // can schedule flag
    private AtomicBoolean canSchedule = new AtomicBoolean(false);

    // init flag
    private AtomicBoolean initFlag = new AtomicBoolean(false);

    // current running task num
    private AtomicInteger runningTaskNum = new AtomicInteger(0);

    // TODO 初始化时进行出入
    private DispatchService dispatchService;


    public void incrRunningTask() {
        runningTaskNum.incrementAndGet();
    }

    public void decrRunningTask() {
        int num = runningTaskNum.decrementAndGet();
        if (num < 0) {
            runningTaskNum.set(0);
        }
    }

    public boolean isInRunning() {
        return runningTaskNum.get() > 0;
    }

    public String getHostname() {
        try {
            return NetworkUtil.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean canScheduler() {
        return canSchedule.get();
    }

    public void stopScheduler() {
        this.canSchedule.set(false);
    }

    public void startScheduler() {
        this.canSchedule.set(true);
    }


    public void clearAllHandler() {
        this.handlerMap.clear();
        this.handlerIdentifyCode.set(0);
    }

    public String getRegisterVersion() {
        return registerVersion;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegister(String registerVersion, long registerTime) {
        this.registerVersion = registerVersion;
        this.registerTime = registerTime;
    }

    public void addHandler(String handler, List<Integer> nodes) {
        handlerMap.put(handler, nodes);
        handlers.add(handler);
    }

    public ConcurrentHashMap<String, List<Integer>> getHandlerMap() {
        return handlerMap;
    }

    public int getHandlerIdentityCode() {
        return handlerIdentifyCode.get();
    }

    public void setHandlerIdentifyCode(int handlerIdentifyCode) {
        this.handlerIdentifyCode.set(handlerIdentifyCode);
    }

    public CopyOnWriteArraySet<String> getHandlers() {
        return handlers;
    }

    public CopyOnWriteArrayList<Integer> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Integer> nodeList) {
        this.nodeList.clear();
        this.nodeList.addAll(nodeList);
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getZkNamespace() {
        return zkNamespace;
    }

    public void setZkNamespace(String zkNamespace) {
        this.zkNamespace = zkNamespace;
    }

    public String getZkRootPath() {
        return zkRootPath;
    }

    public void setZkRootPath(String zkRootPath) {
        this.zkRootPath = zkRootPath;
        this.handlerGroup = zkRootPath.substring(1);
    }

    public int getZkSessionTimeoutMs() {
        return zkSessionTimeoutMs;
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
    }

    public int getZkConnectionTimeoutMs() {
        return zkConnectionTimeoutMs;
    }

    public void setZkConnectionTimeoutMs(int zkConnectionTimeoutMs) {
        this.zkConnectionTimeoutMs = zkConnectionTimeoutMs;
    }

    public int getVirtualNodeNum() {
        return virtualNodeNum;
    }

    public void setVirtualNodeNum(int virtualNodeNum) {
        this.virtualNodeNum = virtualNodeNum;
    }

    public String getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(String handlerGroup) {
        this.handlerGroup = handlerGroup;
        this.zkRootPath = "/" + handlerGroup;
    }

    public int getHandelrCoreSize() {
        return handelrCoreSize;
    }

    public void setHandelrCoreSize(int handelrCoreSize) {
        this.handelrCoreSize = handelrCoreSize;
    }

    public int getHandlerMaxSize() {
        return handlerMaxSize;
    }

    public void setHandlerMaxSize(int handlerMaxSize) {
        this.handlerMaxSize = handlerMaxSize;
    }

    public void setRegisterVersion(String registerVersion) {
        this.registerVersion = registerVersion;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public int getMaxTryTime() {
        return maxTryTime;
    }

    public void setMaxTryTime(int maxTryTime) {
        this.maxTryTime = maxTryTime;
    }

    public DispatchService getDispatchService() {
        return dispatchService;
    }

    public void setDispatchService(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    public boolean isInitOk() {
        return initFlag.get();
    }

    public void initOk() {
        initFlag.set(true);
    }

    /**
     * reset
     */
    public void reset() {
        // 停止 任务分配
        this.stopScheduler();
        this.registerVersion = "0";
        this.registerTime = 100;

    }
}
