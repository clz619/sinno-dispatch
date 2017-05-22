package win.sinno.dispatch.core;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import sun.misc.URLClassPath;
import win.sinno.common.util.IdWorkerUtil;
import win.sinno.common.util.JsonUtil;
import win.sinno.common.util.PropertiesUtil;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.core.service.impl.DispatchServiceImpl;
import win.sinno.dispatch.service.DemoHandlerConverter;
import win.sinno.dispatch.service.spring.SpringLaunchContext;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * dispatch core agent test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/16 13:23
 */
public class DispatchCoreTest {

    private SpringLaunchContext springLaunch;
    private ApplicationContext applicationContext;
    private DispatchService dispatchService;

    private DispatchCore dispatchCore = new DispatchCore();

    {
        springLaunch = new SpringLaunchContext("spring.xml");
        applicationContext = springLaunch.get();
        dispatchService = applicationContext.getBean(DispatchServiceImpl.class);

        dispatchCore.setPerFetchSleepTimeMs(1000);
        dispatchCore.setPerFetchNum(500);
        dispatchCore.setZkAddress("192.168.8.200:2181");
        dispatchCore.setZkNamespace("dispatch-agent");
        dispatchCore.setZkSessionTimeoutMs(10000);
        dispatchCore.setZkConnectionTimeoutMs(10000);
        dispatchCore.setHandlerGroup("yb");
        dispatchCore.setHandlers("demo");
        dispatchCore.setCoreSize(4);
        dispatchCore.setMaxSize(64);
        dispatchCore.setVirtualNodeNum(10);
        dispatchCore.setDivideType(1);
        dispatchCore.setDispatchService(dispatchService);
        dispatchCore.setDispatchHandlerConverter(new DemoHandlerConverter());
    }


    @Test
    public void testCoreAgent() throws Exception {
        dispatchCore.startEngine();

        Thread.sleep(1000000000l);
    }

    @Test
    public void testAddTask1() throws InterruptedException {
        IdWorkerUtil idWorker = new IdWorkerUtil(1);
        long b = System.currentTimeMillis();
        long e = 0;
        AtomicInteger i = new AtomicInteger(0);
        while (true) {
            e = System.currentTimeMillis();
            if ((e - b) > 10000) {
                System.out.println("10s create count:" + i.get());
                i.set(0);
                b = e;
            }
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
            dispatchTaskEntity.setLoadbalance(i.incrementAndGet());
            dispatchTaskEntity.setStatus(0);
            dispatchTaskEntity.setNextExecTime(now.getTime());
            dispatchTaskEntity.setFailStrategy(0);
            dispatchTaskEntity.setRetryTime(0);
            dispatchTaskEntity.setRemark("test");

            Long ret = dispatchCore.addDispatchTask(dispatchTaskEntity);
//            System.out.println("add dispatch task ret " + i + ":" + dispatchTaskEntity.getId() + ":" + ret);

            Thread.sleep(1);
        }

    }

    @Test
    public void testAddTask2() throws InterruptedException {
        IdWorkerUtil idWorker = new IdWorkerUtil(2);
        long b = System.currentTimeMillis();
        long e = 0;
        AtomicInteger i = new AtomicInteger(0);
        while (true) {
            e = System.currentTimeMillis();
            if ((e - b) > 10000) {
                System.out.println("10s create count:" + i.get());
                i.set(0);
                b = e;
            }
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
            dispatchTaskEntity.setLoadbalance(i.incrementAndGet());
            dispatchTaskEntity.setStatus(0);
            dispatchTaskEntity.setNextExecTime(now.getTime());
            dispatchTaskEntity.setFailStrategy(0);
            dispatchTaskEntity.setRetryTime(0);
            dispatchTaskEntity.setRemark("test");

            Long ret = dispatchCore.addDispatchTask(dispatchTaskEntity);
//            System.out.println("add dispatch task ret " + i + ":" + dispatchTaskEntity.getId() + ":" + ret);

            Thread.sleep(1);
        }
    }

    @Test
    public void testAddTask3() throws InterruptedException {
        IdWorkerUtil idWorker = new IdWorkerUtil(3);
        long b = System.currentTimeMillis();
        long e = 0;
        AtomicInteger i = new AtomicInteger(0);
        while (true) {
            e = System.currentTimeMillis();
            if ((e - b) > 10000) {
                System.out.println("10s create count:" + i.get());
                i.set(0);
                b = e;
            }
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
            dispatchTaskEntity.setLoadbalance(i.incrementAndGet());
            dispatchTaskEntity.setStatus(0);
            dispatchTaskEntity.setNextExecTime(now.getTime());
            dispatchTaskEntity.setFailStrategy(0);
            dispatchTaskEntity.setRetryTime(0);
            dispatchTaskEntity.setRemark("test");

            Long ret = dispatchCore.addDispatchTask(dispatchTaskEntity);
//            System.out.println("add dispatch task ret " + i + ":" + dispatchTaskEntity.getId() + ":" + ret);

            Thread.sleep(1);
        }
    }

    @Test
    public void testClassPath() throws IOException {
        URLClassPath urlc = sun.misc.Launcher.getBootstrapClassPath();
        URL[] urls = urlc.getURLs();
        for (URL u : urls) {
            System.out.println(u);
        }

        System.out.println(".............");

        Properties serverProps =
                PropertiesUtil.loadFromResources("server.properties");

        System.out.println(serverProps);

    }
}
