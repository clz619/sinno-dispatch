package win.sinno.dispatch.engine.agent;

/**
 * agent manager
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/15 11:49
 */
public interface IAgentManager {

    // -- zk node agent
    ZkNodeAgent getZkNodeAgent();

    void setZkNodeAgent(ZkNodeAgent zkNodeAgent);

    void startZkNodeAgent() throws Exception;

    // -- handler server cluster status
    HanlderServerClusterStatusAgent getHanlderServerClusterStatusAgent();

    void setHanlderServerClusterStatusAgent(HanlderServerClusterStatusAgent hanlderServerClusterStatusAgent);

    void startHanlderServerClusterStatusAgent() throws Exception;

    // -- event agent
    EventAgent getEventAgent();

    void setEventAgent(EventAgent eventAgent);

    void startEventAgent() throws Exception;
}
