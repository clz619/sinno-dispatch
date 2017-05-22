package win.sinno.dispatch.engine.server;

import win.sinno.concurrent.earthworm.DataQueueCenter;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.engine.agent.*;
import win.sinno.dispatch.engine.dispatch.DispatchHandlerConverter;
import win.sinno.dispatch.engine.dispatch.DispatchResultService;
import win.sinno.dispatch.engine.repository.EventConsumerRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * handelr server
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 15:50
 */
public class HandlerServer implements IAgentManager {


    private String handlerGroup;

    // node
    private int virtualNodeNum = 8;

    // handler coresize
    private int handelrCoreSize = 2;

    // handler maxsize
    private int handlerMaxSize = 5;

    private int maxTryTime = 3;

    /**
     * 每次获取数据休眠时间
     */
    private int perFetchSleepTimeMs = 3000;

    private int perFetchNum = 100;

    private HandlerServerZkConf handlerServerZkConf = new HandlerServerZkConf();

    // dispatch service
    private DispatchService dispatchService;

    // dispatch result logic
    private DispatchResultService dispatchResultService;

    // dispatch handler converter
    private DispatchHandlerConverter dispatchHandlerConverter;

    private HandlerServerRunningData handlerServerRunningData = new HandlerServerRunningData();

    private EventConsumerRepository eventConsumerRepository = new EventConsumerRepository();

    /**
     * server agent manager
     */
    private ServerAgentManager serverAgentManager = new ServerAgentManager();

    private DataQueueCenter dataQueueCenter = new DataQueueCenter();

    public DataQueueCenter getDataQueueCenter() {
        return dataQueueCenter;
    }

    public String getZkAddress() {
        return this.handlerServerZkConf.getZkAddress();
    }

    public void setZkAddress(String zkAddress) {
        this.handlerServerZkConf.setZkAddress(zkAddress);
    }

    public String getZkNamespace() {
        return this.handlerServerZkConf.getZkNamespace();
    }

    public void setZkNamespace(String zkNamespace) {
        this.handlerServerZkConf.setZkNamespace(zkNamespace);
    }

    public String getZkRootPath() {
        return this.handlerServerZkConf.getZkRootPath();
    }

    public void setZkRootPath(String zkRootPath) {
        this.handlerServerZkConf.setZkRootPath(zkRootPath);

        this.handlerGroup = zkRootPath.substring(1);
    }

    public int getZkSessionTimeoutMs() {
        return this.handlerServerZkConf.getZkSessionTimeoutMs();
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        this.handlerServerZkConf.setZkSessionTimeoutMs(zkSessionTimeoutMs);
    }

    public int getZkConnectionTimeoutMs() {
        return this.handlerServerZkConf.getZkConnectionTimeoutMs();
    }

    public void setZkConnectionTimeoutMs(int zkConnectionTimeoutMs) {
        this.handlerServerZkConf.setZkConnectionTimeoutMs(zkConnectionTimeoutMs);
    }

    public HandlerServerZkConf getHandlerServerZkConf() {
        return this.handlerServerZkConf;
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
        this.handlerServerZkConf.setZkRootPath("/" + handlerGroup);
        this.handlerGroup = handlerGroup;
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

    public int getMaxTryTime() {
        return maxTryTime;
    }

    public void setMaxTryTime(int maxTryTime) {
        this.maxTryTime = maxTryTime;
    }

    public int getPerFetchSleepTimeMs() {
        return perFetchSleepTimeMs;
    }

    public void setPerFetchSleepTimeMs(int perFetchSleepTimeMs) {
        this.perFetchSleepTimeMs = perFetchSleepTimeMs;
    }

    public int getPerFetchNum() {
        return perFetchNum;
    }

    public void setPerFetchNum(int perFetchNum) {
        this.perFetchNum = perFetchNum;
    }

    public DispatchService getDispatchService() {
        return dispatchService;
    }

    public void setDispatchService(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }


    public void incrRunningCount() {
        handlerServerRunningData.incrRunningCount();
    }

    public void decrRunningCount() {
        handlerServerRunningData.decrRunningCount();
    }

    public boolean isRunning() {
        return handlerServerRunningData.isRunning();
    }

    public String getHostname() {
        return handlerServerRunningData.getHostname();
    }

    public boolean isCanRunning() {
        return handlerServerRunningData.isCanRunning();
    }

    public void stopRunning() {
        this.handlerServerRunningData.stopRunning();
    }

    public void startRunning() {
        this.handlerServerRunningData.startRunning();
    }


    public void clearAllHandler() {
        this.handlerServerRunningData.clearAllHandler();
    }

    public String getRegisterVersion() {
        return this.handlerServerRunningData.getRegisterVersion();
    }

    public long getRegisterTime() {
        return this.handlerServerRunningData.getRegisterTime();
    }

    public void setRegisterInfo(String registerVersion, long registerTime) {
        this.handlerServerRunningData.setRegisterInfo(registerVersion, registerTime);
    }

    public void addHandler(String handler, List<Integer> nodes) {
        this.handlerServerRunningData.addHandler(handler, nodes);
    }

    public Map<String, List<Integer>> getHandlerRefNodeMap() {
        return this.handlerServerRunningData.getHandlerRefNodeMap();
    }

    public int getHandlerIdentityCode() {
        return this.handlerServerRunningData.getHandlerIdentifyCode();
    }

    public void setHandlerIdentifyCode(int handlerIdentifyCode) {
        this.handlerServerRunningData.setHandlerIdentifyCode(handlerIdentifyCode);
    }

    public Set<String> getHandlerSet() {
        return this.handlerServerRunningData.getHandlerSet();
    }

    public List<Integer> getNodeList() {
        return this.handlerServerRunningData.getNodeList();
    }

    public synchronized void setNodeList(List<Integer> nodeList) {
        this.handlerServerRunningData.setNodeList(nodeList);
    }

    public boolean isInit() {
        return this.handlerServerRunningData.isInit();
    }

    public void initDone() {
        checkInitStatus();
        // 初始化完成
        this.handlerServerRunningData.initDone();
    }

    public void reset() {
        this.handlerServerRunningData.stopRunning();

        this.handlerServerRunningData.setRegisterInfo("0", 100l);
    }

    private void checkInitStatus() {
        // TODO 检测 初始化 数据，必需参数不能为空
    }

    public DispatchResultService getDispatchResultService() {
        return dispatchResultService;
    }

    public void setDispatchResultService(DispatchResultService dispatchResultService) {
        this.dispatchResultService = dispatchResultService;
    }

    public DispatchHandlerConverter getDispatchHandlerConverter() {
        return dispatchHandlerConverter;
    }

    public void setDispatchHandlerConverter(DispatchHandlerConverter dispatchHandlerConverter) {
        this.dispatchHandlerConverter = dispatchHandlerConverter;
    }

    public EventConsumerRepository getEventConsumerRepository() {
        return eventConsumerRepository;
    }

    @Override
    public ZkNodeAgent getZkNodeAgent() {
        return serverAgentManager.getZkNodeAgent();
    }

    @Override
    public void setZkNodeAgent(ZkNodeAgent zkNodeAgent) {
        serverAgentManager.setZkNodeAgent(zkNodeAgent);
    }

    @Override
    public void startZkNodeAgent() throws Exception {
        serverAgentManager.startZkNodeAgent();
    }

    @Override
    public HanlderServerClusterStatusAgent getHanlderServerClusterStatusAgent() {
        return serverAgentManager.getHanlderServerClusterStatusAgent();
    }

    @Override
    public void setHanlderServerClusterStatusAgent(HanlderServerClusterStatusAgent hanlderServerClusterStatusAgent) {
        serverAgentManager.setHanlderServerClusterStatusAgent(hanlderServerClusterStatusAgent);
    }

    @Override
    public void startHanlderServerClusterStatusAgent() throws Exception {
        serverAgentManager.startHanlderServerClusterStatusAgent();
    }

    @Override
    public EventAgent getEventAgent() {
        return serverAgentManager.getEventAgent();
    }

    @Override
    public void setEventAgent(EventAgent eventAgent) {
        serverAgentManager.setEventAgent(eventAgent);
    }

    @Override
    public void startEventAgent() throws Exception {
        serverAgentManager.startEventAgent();
    }

}
