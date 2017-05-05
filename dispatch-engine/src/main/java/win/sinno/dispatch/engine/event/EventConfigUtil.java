package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.engine.ScheduleServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * event执行 配置工具
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 14:14.
 */
public class EventConfigUtil {

    /**
     * sync event config
     *
     * @return
     */
    public static List<EventConfig> syncEventConfig() {
        List<EventConfig> eventConfigs = new ArrayList<>();
        //handler map
        Map<String, List<Integer>> hanlderMap = ScheduleServer.getInstance().getHandlerMap();
        //handler 识别码
        int identifyCode = ScheduleServer.getInstance().getHandlerIdentityCode();

        for (Map.Entry<String, List<Integer>> entry : hanlderMap.entrySet()) {
            String handlerName = entry.getKey();
            List<Integer> nodeList = entry.getValue();

            EventConfig eventConfig = new EventConfig();
            eventConfig.setHandler(handlerName);
            eventConfig.setNodeList(nodeList);
            eventConfig.setIdentifyCode(identifyCode);

            eventConfigs.add(eventConfig);
        }

        return eventConfigs;
    }
}
