package win.sinno.dispathc.engine;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 17:49
 */
public class StringUtilTets {

    @Test
    public void testJoin() {
        List<String> a = new ArrayList<>();

        a.add("a");
        a.add("b");
        a.add("c");

        System.out.println(StringUtils.join(a.toArray(new String[a.size()]), "_"));
    }
}
