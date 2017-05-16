package win.sinno.dispatch.engine.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.event.EventConfig;
import win.sinno.dispatch.engine.event.EventExecutorAgent;
import win.sinno.dispatch.engine.server.HandlerServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * event agent
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/15 15:39
 */
public class EventAgent implements IAgent {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private HandlerServer server;

    private EventExecutorAgent eventExecutorAgent;

    private Thread eventAgentThread;

    public EventAgent(final HandlerServer handlerServer) {
        this.server = handlerServer;

        eventExecutorAgent = new EventExecutorAgent(server);

        eventAgentThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (server.isCanRunning()) {
                            if (!eventExecutorAgent.isInit()) {
                                init();
                            }

                            // 丢线程执行事件
                            eventExecutorAgent.execute();
                        }

                        if (eventExecutorAgent.isInit()) {
                            // 轮询时间
                            Thread.sleep(server.getSleepTimeMsPerFetch());
                        } else {
                            Thread.sleep(1000);
                            continue;
                        }

                        if (eventExecutorAgent.getCurrentExecHandlerIdentityCode() != server.getHandlerIdentityCode()) {
                            Thread.sleep(3000);
                            init();
                        }
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }

            private void init() {

                List<EventConfig> eventConfigs = new ArrayList<>();

                Map<String, List<Integer>> hanlderMap = handlerServer.getHandlerRefNodeMap();

                int identifyCode = handlerServer.getHandlerIdentityCode();

                for (Map.Entry<String, List<Integer>> entry : hanlderMap.entrySet()) {
                    String handlerName = entry.getKey();
                    List<Integer> nodeList = entry.getValue();

                    EventConfig eventConfig = new EventConfig();
                    eventConfig.setHandler(handlerName);
                    eventConfig.setNodeList(nodeList);
                    eventConfig.setHandlerIdentifyCode(identifyCode);

                    eventConfigs.add(eventConfig);
                }

                eventExecutorAgent.init(eventConfigs);
            }
        };

        eventAgentThread.setDaemon(true);
        eventAgentThread.setName("EventAgent");
    }


    /**
     * agent start
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        eventAgentThread.start();
    }
}
