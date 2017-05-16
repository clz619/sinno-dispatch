package win.sinno.dispathc.engine.zk;

import org.junit.Test;
import win.sinno.common.util.PropertiesUtil;
import win.sinno.dispatch.engine.server.HandlerServer;
import win.sinno.dispatch.engine.constant.ZkProps;
import win.sinno.dispatch.engine.agent.ZkNodeAgent;

import java.io.IOException;
import java.util.Properties;

/**
 * zk node agent test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 09:51.
 */
public class ZkNodeAgentTest {

    private Properties properties = PropertiesUtil.loadFromResources("zk.properties");

    public ZkNodeAgentTest() throws IOException {
    }

    @Test
    public void testZkRegister() {
        //执行服务器
//        HandlerServer scheduleServer = HandlerServer.getInstance();
//
//        scheduleServer.setZkAddress(properties.getProperty(ZkProps.ZK_ADDRESS));
//        scheduleServer.setZkConnectionTimeoutMs(Integer.valueOf(properties.getProperty(ZkProps.ZK_CONNECTION_TIMEOUT)));
//        scheduleServer.setZkSessionTimeoutMs(Integer.valueOf(properties.getProperty(ZkProps.ZK_SESSION_TIMEOUT)));
//        scheduleServer.setZkNamespace(properties.getProperty(ZkProps.ZK_NAMESPACE));
//        scheduleServer.setZkRootPath(properties.getProperty(ZkProps.ZK_ROOT_PATH));
//
//        ZkNodeAgent zkNodeAgent = new ZkNodeAgent();
//        zkNodeAgent.setZkAddress(scheduleServer.getZkAddress());
//        zkNodeAgent.setConnectionTimeoutMs(scheduleServer.getZkConnectionTimeoutMs());
//        zkNodeAgent.setSessionTimeoutMs(scheduleServer.getZkSessionTimeoutMs());
//        zkNodeAgent.setNamespace(scheduleServer.getZkNamespace());
//        zkNodeAgent.setRootPath(scheduleServer.getZkRootPath());
//
//        try {
//            zkNodeAgent.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
