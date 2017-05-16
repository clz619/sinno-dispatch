package win.sinno.dispatch.engine.agent;

/**
 * server agent manager
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/15 11:35
 */
public class ServerAgentManager implements IAgentManager {

    // zk node agent
    private ZkNodeAgent zkNodeAgent;

    private HanlderServerClusterStatusAgent hanlderServerClusterStatusAgent;

    private EventAgent eventAgent;

    public ZkNodeAgent getZkNodeAgent() {
        return zkNodeAgent;
    }

    public void setZkNodeAgent(ZkNodeAgent zkNodeAgent) {
        this.zkNodeAgent = zkNodeAgent;
    }

    @Override
    public void startZkNodeAgent() throws Exception {
        zkNodeAgent.start();
    }

    @Override
    public HanlderServerClusterStatusAgent getHanlderServerClusterStatusAgent() {
        return hanlderServerClusterStatusAgent;
    }

    @Override
    public void setHanlderServerClusterStatusAgent(HanlderServerClusterStatusAgent hanlderServerClusterStatusAgent) {
        this.hanlderServerClusterStatusAgent = hanlderServerClusterStatusAgent;
    }

    @Override
    public void startHanlderServerClusterStatusAgent() throws Exception {
        hanlderServerClusterStatusAgent.start();
    }

    @Override
    public EventAgent getEventAgent() {
        return eventAgent;
    }

    @Override
    public void setEventAgent(EventAgent eventAgent) {
        this.eventAgent = eventAgent;
    }

    @Override
    public void startEventAgent() throws Exception {
        this.eventAgent.start();
    }

}
