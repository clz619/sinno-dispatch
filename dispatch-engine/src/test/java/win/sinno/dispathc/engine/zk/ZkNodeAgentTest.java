package win.sinno.dispathc.engine.zk;

import org.junit.Test;
import win.sinno.dispatch.engine.ScheduleServer;
import win.sinno.dispatch.engine.zk.ZkNodeAgent;

/**
 * zk node agent test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 09:51.
 */
public class ZkNodeAgentTest {

    @Test
    public void testZkRegister() {
        //执行服务器
        ScheduleServer scheduleServer = ScheduleServer.getInstance();

        scheduleServer.setZkAddress("10.1.1.111:2181");
        scheduleServer.setZkConnectionTimeoutMs(60000);
        scheduleServer.setZkSessionTimeoutMs(60000);
        scheduleServer.setZkNamespace("sinno");
        scheduleServer.setHandlerGroup("dispatch");

        ZkNodeAgent zkNodeAgent = new ZkNodeAgent();
        zkNodeAgent.setZkAddress(scheduleServer.getZkAddress());
        zkNodeAgent.setConnTimeoutMs(scheduleServer.getZkConnectionTimeoutMs());
        zkNodeAgent.setSessionTimeoutMs(scheduleServer.getZkSessionTimeoutMs());
        zkNodeAgent.setNamespace(scheduleServer.getZkNamespace());
        zkNodeAgent.setPath(scheduleServer.getZkRootPath());
        try {
            zkNodeAgent.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
