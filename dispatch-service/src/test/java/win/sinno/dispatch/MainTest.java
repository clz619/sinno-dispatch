package win.sinno.dispatch;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import win.sinno.common.util.IdWorkerUtil;
import win.sinno.common.util.PropertiesUtil;
import win.sinno.dispatch.api.DispatchService;
import win.sinno.dispatch.core.service.impl.DispatchServiceImpl;
import win.sinno.dispatch.engine.ScheduleManagerFactory;
import win.sinno.dispatch.engine.constant.ScheduleProps;
import win.sinno.dispatch.service.spring.SpringLaunchContext;

import java.util.Properties;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 16:34
 */
public class MainTest {

    private IdWorkerUtil idWorker = new IdWorkerUtil(1);

    private SpringLaunchContext springLaunch;

    private ApplicationContext applicationContext;

    private DispatchService dispatchService;

    private String handlerGroup = "sinno";

    {
        springLaunch = new SpringLaunchContext("spring.xml");
        applicationContext = springLaunch.get();
        dispatchService = applicationContext.getBean(DispatchServiceImpl.class);
    }

    @Test
    public void test() throws Exception {
        ScheduleManagerFactory scheduleManagerFactory = new ScheduleManagerFactory(30000);

        Properties properties = new Properties();

        // zk props
        Properties zkProps = PropertiesUtil.loadFromResources("zk.properties");

        properties.putAll(zkProps);

        properties.put(ScheduleProps.DISPATCH_SERVICE, dispatchService);

        Properties scheduleProps = PropertiesUtil.loadFromResources("schedule.properties");

        properties.putAll(scheduleProps);

        scheduleManagerFactory.initScheduleServer(properties);

        Thread.sleep(1000000l);
    }
}
