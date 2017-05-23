package win.sinno.dispatch.engine.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * event fetch info
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/23 10:29
 */
public class EventFetchStat implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger("dispatch");

    private AtomicLong fetchNum = new AtomicLong(0);

    private AtomicLong filterNum = new AtomicLong(0);

    private AtomicLong successNum = new AtomicLong(0);

    private AtomicLong failNum = new AtomicLong(0);

    private long initTs = System.currentTimeMillis();

    private volatile long lastFetchTs = 0;

    private long maxFetchPerTs = 20000;

    private long minFetchPerTs = 1000;

    private long defaultFetchPerTs = 5000;

    private volatile long nowFetchPerTs = 5000;

    private long fetchTsStep = 1000;

    private long lastFetchNum = 0;

    private int defaultFetchNum = 100;

    private int nowFetchNum = 100;

    private int fetchNumStep = 10;

    private LongIntegerTuple lastDealSpeed;

    private LongIntegerTuple lastFilterSpeed;

    // key : second
    private Cache<Long, AtomicInteger> filterSpeed = CacheBuilder.newBuilder()
            .maximumSize(4)
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<Long, AtomicInteger>() {
                @Override
                public void onRemoval(RemovalNotification<Long, AtomicInteger> notification) {

                    Long time = notification.getKey();
                    Integer sp = notification.getValue().get();
                    lastFilterSpeed = new LongIntegerTuple(time, sp);

                    LOG.info("time:{} , filter speed :{}", new Object[]{time, sp});

                    if (sp > 100) { //TODO 将逻辑迁移至其它地方
                        incrNowFetchPerTs();
                    }
                }
            })
            .build();

    // key : second
    private Cache<Long, AtomicInteger> dealSpeed = CacheBuilder.newBuilder()
            .maximumSize(4)
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<Long, AtomicInteger>() {
                @Override
                public void onRemoval(RemovalNotification<Long, AtomicInteger> notification) {

                    Long time = notification.getKey();
                    Integer sp = notification.getValue().get();
                    lastDealSpeed = new LongIntegerTuple(time, sp);

                    LOG.info("time:{} , deal speed :{}", new Object[]{time, sp});
                }
            })
            .build();

    public long incrFetch(long num) {
        return fetchNum.addAndGet(num);
    }

    public long getFetchNum() {
        return fetchNum.get();
    }

    public long incrFilter(long num) {
        addFilter();
        return filterNum.addAndGet(num);
    }

    public long getFilterNum() {
        return filterNum.get();
    }

    public long incrSuccess(long num) {
        addSpeed();
        return successNum.addAndGet(num);
    }

    public long getSuccessNum() {
        return successNum.get();
    }

    public long incrFail(long num) {
        return failNum.addAndGet(num);
    }

    public long getFailNum() {
        return failNum.get();
    }

    public long getLastFetchTs() {
        return lastFetchTs;
    }

    public void setLastFetchTs(long lastFetchTs) {
        this.lastFetchTs = lastFetchTs;
    }

    public long getLastFetchNum() {
        return lastFetchNum;
    }

    public void setLastFetchNum(long lastFetchNum) {
        this.lastFetchNum = lastFetchNum;
    }

    public int getDefaultFetchNum() {
        return defaultFetchNum;
    }

    public void setDefaultFetchNum(int defaultFetchNum) {
        this.defaultFetchNum = defaultFetchNum;
    }

    public long getMaxFetchPerTs() {
        return maxFetchPerTs;
    }

    public void setMaxFetchPerTs(long maxFetchPerTs) {
        this.maxFetchPerTs = maxFetchPerTs;
    }

    public long getMinFetchPerTs() {
        return minFetchPerTs;
    }

    public void setMinFetchPerTs(long minFetchPerTs) {
        this.minFetchPerTs = minFetchPerTs;
    }

    public long getInitTs() {
        return initTs;
    }

    public void setInitTs(long initTs) {
        this.initTs = initTs;
    }

    public long getDefaultFetchPerTs() {
        return defaultFetchPerTs;
    }

    public void setDefaultFetchPerTs(long defaultFetchPerTs) {
        this.defaultFetchPerTs = defaultFetchPerTs;
    }

    public long getNowFetchPerTs() {
        return nowFetchPerTs;
    }

    public void setNowFetchPerTs(long nowFetchPerTs) {
        this.nowFetchPerTs = nowFetchPerTs;
    }

    public void incrNowFetchPerTs() {
        synchronized (this) {
            if (nowFetchPerTs < maxFetchPerTs) {
                this.nowFetchPerTs = this.nowFetchPerTs + this.fetchTsStep;
            }
        }
    }

    public void decrNowFetchPerTs() {
        synchronized (this) {
            if (nowFetchPerTs > minFetchPerTs) {
                this.nowFetchPerTs = this.nowFetchPerTs - this.fetchTsStep;
            }
        }
    }

    public long getFetchTsStep() {
        return fetchTsStep;
    }

    public void setFetchTsStep(long fetchTsStep) {
        this.fetchTsStep = fetchTsStep;
    }

    public int getNowFetchNum() {
        return nowFetchNum;
    }

    public void setNowFetchNum(int nowFetchNum) {
        this.nowFetchNum = nowFetchNum;
    }

    public int getFetchNumStep() {
        return fetchNumStep;
    }

    public void setFetchNumStep(int fetchNumStep) {
        this.fetchNumStep = fetchNumStep;
    }

    private Long getNowSecond() {
        Long ts = System.currentTimeMillis();
        return ts / 1000;
    }

    private void addSpeed() {
        Long key = getNowSecond();
        AtomicInteger speed = dealSpeed.getIfPresent(key);
        if (speed == null) {
            synchronized (dealSpeed) {
                speed = dealSpeed.getIfPresent(key);
                if (speed == null) {
                    speed = new AtomicInteger();
                    dealSpeed.put(key, speed);
                }
            }
        }

        speed.incrementAndGet();
    }

    private void addFilter() {
        Long key = getNowSecond();
        AtomicInteger speed = filterSpeed.getIfPresent(key);
        if (speed == null) {
            synchronized (filterSpeed) {
                speed = filterSpeed.getIfPresent(key);
                if (speed == null) {
                    speed = new AtomicInteger();
                    filterSpeed.put(key, speed);
                }
            }
        }

        speed.incrementAndGet();
    }

    @Override
    public String toString() {
        return "EventFetchStat{" +
                "initTs=" + initTs +
                ", lastFetchTs=" + lastFetchTs +
                ", nowFetchPerTs=" + nowFetchPerTs +
                ", lastFetchNum=" + lastFetchNum +
                ", defaultFetchNum=" + defaultFetchNum +
                ",fetchNum=" + fetchNum.get() +
                ", filterNum=" + filterNum.get() +
                ", successNum=" + successNum.get() +
                ", failNum=" + failNum.get() +
                ", lastDealSpeed=" + lastDealSpeed +
                ", lastFilterSpeed=" + lastFilterSpeed +
                '}';
    }

    /**
     * long integer tuple
     */
    class LongIntegerTuple {

        Long secondTs;

        Integer speed;

        public LongIntegerTuple(Long secondTs, Integer speed) {
            this.secondTs = secondTs;
            this.speed = speed;
        }

        public Long getSecondTs() {
            return secondTs;
        }

        public void setSecondTs(Long secondTs) {
            this.secondTs = secondTs;
        }

        public Integer getSpeed() {
            return speed;
        }

        public void setSpeed(Integer speed) {
            this.speed = speed;
        }

        @Override
        public String toString() {
            return "LongIntegerTuple{" +
                    "secondTs=" + secondTs +
                    ", speed=" + speed +
                    '}';
        }
    }
}
