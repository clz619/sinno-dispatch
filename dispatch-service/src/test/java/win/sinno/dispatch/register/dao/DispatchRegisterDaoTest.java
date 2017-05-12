package win.sinno.dispatch.register.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import win.sinno.common.util.IdWorkerUtil;
import win.sinno.common.util.NetworkUtil;
import win.sinno.dispatch.engine.util.VersionGenerator;
import win.sinno.dispatch.register.model.DispatchRegister;
import win.sinno.dispatch.service.spring.SpringLaunchContext;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 14:51
 */
public class DispatchRegisterDaoTest {

    private IdWorkerUtil idWorker = new IdWorkerUtil(1);

    private SpringLaunchContext springLaunch;

    private ApplicationContext applicationContext;

    private DispatchRegisterDao dispatchRegisterDao;

    private String handlerGroup = "sinno";

    {
        springLaunch = new SpringLaunchContext("spring.xml");
        applicationContext = springLaunch.get();
        dispatchRegisterDao = applicationContext.getBean(DispatchRegisterDao.class);
    }

    @Test
    public void testAdd() throws UnknownHostException {
        Date now = new Date();

        String hostname = NetworkUtil.getHostName() + now.getTime();
        long registerTime = now.getTime();
        String nodes = "1,3,4";
        String registerVerison = "" + Math.abs(hostname.hashCode());
        long version = 0;

        DispatchRegister dispatchRegister = new DispatchRegister();
        dispatchRegister.setId(idWorker.nextId());
        dispatchRegister.setGmtCreate(now);
        dispatchRegister.setHandlerGroup(handlerGroup);
        dispatchRegister.setHostname(hostname);
        dispatchRegister.setRegisterTime(registerTime);
        dispatchRegister.setNodes(nodes);
        dispatchRegister.setRegisterVersion(registerVerison);
        dispatchRegister.setVersion(version);

        Long ret = dispatchRegisterDao.insert(dispatchRegister);
        System.out.println("add ret:" + ret);
    }

    @Test
    public void testSelect() {

        Date now = new Date();

        DispatchRegister dispatchRegister = dispatchRegisterDao.selectLastRegister(handlerGroup);
        System.out.println(dispatchRegister);

        String hostname = "lizhongchendeMacBook-Pro.local1494572914279";
        DispatchRegister dispatchRegister1 = dispatchRegisterDao.selectRegister(handlerGroup, hostname);
        System.out.println(dispatchRegister1);

        List<DispatchRegister> dispatchRegisterList = dispatchRegisterDao.selectRegisters(handlerGroup);
        System.out.println(dispatchRegisterList);

        List<String> serverList = new ArrayList<>();
        for (DispatchRegister register : dispatchRegisterList) {
            serverList.add(register.getHostname());
        }


        String registerVersion = VersionGenerator.version(serverList);
        String nodes = "11,22,33";
        String handlerGroup = this.handlerGroup;
        long version = dispatchRegister1.getVersion();

        Long ret = dispatchRegisterDao.updateRegisterWithVersion(registerVersion, now.getTime(), nodes, handlerGroup, hostname, version);
        System.out.println("update register ret:" + ret);

        dispatchRegister = dispatchRegisterDao.selectLastRegister(handlerGroup);
        System.out.println(dispatchRegister);
    }

}
