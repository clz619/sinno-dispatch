package win.sinno.dispatch.service.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring launch
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 10:41
 */
public class SpringLaunchContext implements SpringSupport {

    private ApplicationContext applicationContext;

    private String configLocation;

    public SpringLaunchContext(String configLocation) {
        this.configLocation = configLocation;
        applicationContext = new ClassPathXmlApplicationContext(configLocation);
    }

    @Override
    public ApplicationContext get() {
        return applicationContext;
    }
}
