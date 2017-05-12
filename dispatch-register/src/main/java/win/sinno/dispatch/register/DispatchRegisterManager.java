package win.sinno.dispatch.register;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import win.sinno.dispatch.api.DispatchContext;
import win.sinno.dispatch.register.dao.DispatchRegisterDao;
import win.sinno.dispatch.register.model.DispatchRegister;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * dispatch register for dispatch context
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 13:40
 */
@Component
public class DispatchRegisterManager {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch-register");

    /**
     * key:handler group
     */
    private ConcurrentMap<String, DispatchContext> dispatchContextMap = new ConcurrentHashMap<>();

    @Autowired
    private DispatchRegisterDao dispatchRegisterDao;

    public void init() {

        final Thread registerThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        for (Map.Entry<String, DispatchContext> entry : dispatchContextMap.entrySet()) {
                            String handlerGroup = entry.getKey();
                            DispatchContext dispatchContext = entry.getValue();

                            //获取最后更新的机器，以最后更新的机器的版本信息为准
                            DispatchRegister register = dispatchRegisterDao.selectLastRegister(handlerGroup);

                            if (register == null
                                    || StringUtils.equals(dispatchContext.getRegisterVersion(), register.getRegisterVersion())) {
                                continue;
                            }

                            //更新本地的上下文对象
                            dispatchContext.setRegisterVersion(register.getRegisterVersion());
                            dispatchContext.setRegisterTime(register.getRegisterTime());
                            dispatchContext.setHostName(register.getHostname());
                        }
                    } catch (Exception e) {
                        LOG.error("loop query dispatch register exception." + dispatchContextMap, e);
                    } finally {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            }
        };
        registerThread.setDaemon(true);
        registerThread.setName("dispatch-register-thread");
        registerThread.start();
    }

    /**
     * register
     *
     * @param context
     * @return
     */
    public boolean register(DispatchContext context) {
        String handlerGroup = context.getHandlerGroup();

        DispatchContext existContext = dispatchContextMap.putIfAbsent(handlerGroup, context);

        if (existContext != null) {
            if ((context.getRegisterTime() - 2) > existContext.getRegisterTime()) {
                // deps register time update context.
                existContext = context;
            }
        }

        String hostName = context.getHostName();

        DispatchRegister register = dispatchRegisterDao.selectRegister(handlerGroup, hostName);
        if (register == null) {
            //未注册过
            register = new DispatchRegister();
            register.setHandlerGroup(handlerGroup);
            register.setHostname(hostName);
            register.setRegisterVersion(context.getRegisterVersion());
            register.setRegisterTime(context.getRegisterTime());

            if (context.getNodeList().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int node : context.getNodeList()) {
                    sb.append(node).append(",");
                }
                register.setNodes(sb.substring(0, sb.length() - 1));
            }
            register.setVersion(0);
            try {
                long id = dispatchRegisterDao.insert(register);
                if (id > 0) {
                    return true;
                }
            } catch (Exception e) {
                LOG.error("add register 2 db exception," + context, e);
            }
        } else {
            String nodes = null;
            if (context.getNodeList().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int node : context.getNodeList()) {
                    sb.append(node).append(",");
                }
                nodes = sb.substring(0, sb.length() - 1);
            }
            Long num = dispatchRegisterDao.updateRegisterWithVersion(context.getRegisterVersion(),
                    context.getRegisterTime(), nodes, handlerGroup, hostName, register.getVersion());

            return (num > 0);
        }

        return false;
    }

    /**
     * 是否有效
     *
     * @param context
     * @return
     */
    public boolean isValid(DispatchContext context) {
        String handlerGroup = context.getHandlerGroup();
        String registerVersion = context.getRegisterVersion();
        DispatchContext registerContext = dispatchContextMap.get(handlerGroup);

        if (registerContext == null) {
            DispatchRegister register = dispatchRegisterDao.selectLastRegister(handlerGroup);
            if (register == null) {
                return false;
            }

            registerContext = new DispatchContext();
            registerContext.setHandlerGroup(register.getHandlerGroup());
            registerContext.setHostName(register.getHostname());
            registerContext.setRegisterVersion(register.getRegisterVersion());
            registerContext.setRegisterTime(register.getRegisterTime());

            DispatchContext existContext = dispatchContextMap.putIfAbsent(handlerGroup, registerContext);
            if (existContext != null) {
                registerContext = existContext;
            }
        }
        //
        if (StringUtils.equals(registerVersion, registerContext.getRegisterVersion())) {
            return true;
        }

        if (context.getRegisterTime() > (registerContext.getRegisterTime() + 5)) {
            // check register and if need , do update
            DispatchRegister currentRegister = dispatchRegisterDao.selectLastRegister(handlerGroup);

            if (currentRegister == null) {
                return false;
            }

            if (StringUtils.equals(registerVersion, currentRegister.getRegisterVersion())) {
                //update context map
                dispatchContextMap.put(handlerGroup, context);
                return true;
            }
        }

        return false;
    }

    public List<DispatchRegister> selectRegisters(String handlerGroup) {
        if (StringUtils.isBlank(handlerGroup)) {
            return Collections.emptyList();
        }

        return dispatchRegisterDao.selectRegisters(handlerGroup);
    }
}
