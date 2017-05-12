package win.sinno.dispatch.service.spring.launch;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import win.sinno.common.util.IdWorkerUtil;
import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.api.TaskAttribute;
import win.sinno.dispatch.biz.service.impl.DispatchServiceImpl;
import win.sinno.dispatch.service.spring.SpringLaunchContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 10:46
 */

public class SpringLaunchTest {

    private IdWorkerUtil idWorker = new IdWorkerUtil(1);

    private SpringLaunchContext springLaunch;

    private ApplicationContext applicationContext;

    private DispatchServiceImpl dispatchService;

    {
        springLaunch = new SpringLaunchContext("spring.xml");
        applicationContext = springLaunch.get();
        dispatchService = applicationContext.getBean(DispatchServiceImpl.class);
    }

    @Test
    public void a() {
        System.out.println(151 % 20);
        System.out.println(new Date(1494567113699l));
    }

    @Test
    public void testAdd() {

        for (int i = 1; i < 20; i++) {
            Date now = new Date();
            DispatchTaskEntity dispatchTaskEntity = new DispatchTaskEntity();
            dispatchTaskEntity.setId(idWorker.nextId());
            dispatchTaskEntity.setGmtCreate(now);
            dispatchTaskEntity.setBizUniqueId("biz" + idWorker.nextId());
            dispatchTaskEntity.setParameter("{}");
            dispatchTaskEntity.setTraceId("" + idWorker.nextId());
            dispatchTaskEntity.setHandlerGroup("sinno");
            dispatchTaskEntity.setHandler("s1");
            dispatchTaskEntity.setNode(i % 10);
            dispatchTaskEntity.setLoadbalance(i);
            dispatchTaskEntity.setStatus(0);
            dispatchTaskEntity.setNextExecTime(now.getTime());
            dispatchTaskEntity.setFailStrategy(0);
            dispatchTaskEntity.setRetryTime(0);
            dispatchTaskEntity.setRemark("test");

            Long ret = dispatchService.addDispatchTask(dispatchTaskEntity);
            System.out.println("add dispatch task ret:" + ret);
        }

    }

    @Test
    public void testSelect() {
        String handlerGroup = "sinno";

        List<Integer> nodes = new ArrayList<>();
        nodes.add(2);
        nodes.add(5);

        int limit = 10;

        List<DispatchTaskEntity> list = dispatchService.findDispatchTasksWithLimit(handlerGroup, nodes, limit, null);
        System.out.println("get info size:" + list.size());
        for (DispatchTaskEntity entity : list) {
            System.out.println(entity);
        }

    }

    @Test
    public void testUpdate() {

        long taskId = 11775657616883712l;
        Date date = new Date();

        boolean ret = dispatchService.addRetryTimesAndExecuteTime(taskId, date, null);
        System.out.println("update ret:" + ret);
    }

    @Test
    public void testCancelWithTaskId() {

        String handlerGroup = "sinno";

        long taskId = 11775657616883712l;
        TaskAttribute taskAttribute = new TaskAttribute();
        taskAttribute.setTaskId(taskId);

        boolean ret = dispatchService.removeDispatchTask(handlerGroup, taskAttribute);
        System.out.println("remove ret:" + ret);
    }

    @Test
    public void testCancelWithBizId() {

        String handlerGroup = "sinno";

        String bizUniqueId = "biz11779151354675201";
        TaskAttribute taskAttribute = new TaskAttribute();
        taskAttribute.setBizUniqueId(bizUniqueId);

        boolean ret = dispatchService.removeDispatchTask(handlerGroup, taskAttribute);
        System.out.println("remove ret:" + ret);
    }
}
