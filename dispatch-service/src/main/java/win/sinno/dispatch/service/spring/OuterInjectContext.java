package win.sinno.dispatch.service.spring;

import org.springframework.context.ApplicationContext;

/**
 * outer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 11:47
 */
public class OuterInjectContext implements SpringSupport {

    private ApplicationContext applicationContext;

    public OuterInjectContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext get() {
        return applicationContext;
    }
}
