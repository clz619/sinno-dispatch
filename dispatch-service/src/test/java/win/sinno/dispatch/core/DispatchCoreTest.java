package win.sinno.dispatch.core;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import win.sinno.common.util.IdWorkerUtil;
import win.sinno.common.util.JsonUtil;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.core.service.impl.DispatchServiceImpl;
import win.sinno.dispatch.service.HandlerConverter;
import win.sinno.dispatch.service.spring.SpringLaunchContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * dispatch core agent test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/16 13:23
 */
public class DispatchCoreTest {

    private IdWorkerUtil idWorker = new IdWorkerUtil(1);

    private SpringLaunchContext springLaunch;
    private ApplicationContext applicationContext;
    private DispatchService dispatchService;

    private DispatchCore dispatchCore = new DispatchCore();

    {
        springLaunch = new SpringLaunchContext("spring.xml");
        applicationContext = springLaunch.get();
        dispatchService = applicationContext.getBean(DispatchServiceImpl.class);

        dispatchCore.setSleepPerFetchTimeMs(3000);
        dispatchCore.setZkAddress("192.168.8.200:2181");
        dispatchCore.setZkNamespace("dispatch-agent");
        dispatchCore.setZkSessionTimeoutMs(10000);
        dispatchCore.setZkConnectionTimeoutMs(10000);
        dispatchCore.setHandlerGroup("yb");
        dispatchCore.setHandlers("demo");
        dispatchCore.setCoreSize(2);
        dispatchCore.setMaxSize(4);
        dispatchCore.setVirtualNodeNum(10);
        dispatchCore.setDivideType(1);
        dispatchCore.setDispatchService(dispatchService);
        dispatchCore.setDispatchHandlerConverter(new HandlerConverter());
    }


    @Test
    public void testCoreAgent() throws Exception {
        dispatchCore.startEngine();

        Thread.sleep(100000000l);
    }

    @Test
    public void testAddTask() throws InterruptedException {
        for (int i = 1; i < 200; i++) {
            Map<String, String> params = new HashMap<>();
            params.put("handlerGroup", "yb");
            params.put("handler", "demo");
            params.put("str", "hello:" + i);

            Date now = new Date();
            DispatchTaskEntity dispatchTaskEntity = new DispatchTaskEntity();
            dispatchTaskEntity.setId(idWorker.nextId());
            dispatchTaskEntity.setGmtCreate(now);
            dispatchTaskEntity.setBizUniqueId("biz" + idWorker.nextId());
            dispatchTaskEntity.setParameter(JsonUtil.toJson(params));
            dispatchTaskEntity.setTraceId("" + idWorker.nextId());
            dispatchTaskEntity.setHandlerGroup("yb");
            dispatchTaskEntity.setHandler("demo");
            dispatchTaskEntity.setLoadbalance(i);
            dispatchTaskEntity.setStatus(0);
            dispatchTaskEntity.setNextExecTime(now.getTime());
            dispatchTaskEntity.setFailStrategy(0);
            dispatchTaskEntity.setRetryTime(0);
            dispatchTaskEntity.setRemark("test");

            Long ret = dispatchCore.addDispatchTask(dispatchTaskEntity);
            System.out.println("add dispatch task ret:" + ret);

            Thread.sleep(10000l);
        }
    }
}
