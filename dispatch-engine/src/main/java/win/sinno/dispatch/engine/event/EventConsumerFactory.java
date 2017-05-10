package win.sinno.dispatch.engine.event;

import win.sinno.dispatch.api.DispatchTaskEntity;
import win.sinno.dispatch.engine.dispatch.DispatchHandler;
import win.sinno.dispatch.engine.dispatch.DispatchResultManager;

/**
 * event consumer factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-10 16:22.
 */
public final class EventConsumerFactory {

    public static EventConsumer create(DispatchTaskEntity dispatchTaskEntity, EventConfig eventConfig) {

        // 事件消费器
        String handler = dispatchTaskEntity.getHandler();

        DispatchHandler dispatchHandler = null;// TODO 根据task.handler进行 处理器 映射

        if (dispatchHandler == null) {

        }

        EventConsumer eventConsumer = new EventConsumer(dispatchTaskEntity, dispatchHandler, DispatchResultManager.getInstance(), eventConfig.getIdentifyCode());

        return eventConsumer;
    }
}
