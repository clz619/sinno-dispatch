package win.sinno.dispatch.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.dispatch.engine.event.EventExecutor;
import win.sinno.dispatch.engine.event.EventExecutorManager;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * event executor scheduler
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-10 17:59.
 */
public class EventExecutorScheduler {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private static class EventExecutorSchedulerHolder {
        private static final EventExecutorScheduler HOLDER = new EventExecutorScheduler();
    }

    public static EventExecutorScheduler getInstance() {
        return EventExecutorSchedulerHolder.HOLDER;
    }

    private static ThreadPoolExecutor threadPoolExecutor;

    private EventExecutorScheduler() {
        threadPoolExecutor = new ThreadPoolExecutor(10, 15, 10l, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000)
                , new ThreadFactory() {
            AtomicInteger index = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("EventExecutorScheduler#" + index.incrementAndGet());
                return thread;
            }
        });
    }

    public void execute() {
        List<EventExecutor> eventExecutors = EventExecutorManager.getInstance()
                .getEventExecutors();
        for (final EventExecutor eventExecutor : eventExecutors) {
            try {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        eventExecutor.doWork();
                    }
                });
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
