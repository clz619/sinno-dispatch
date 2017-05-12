package win.sinno.dispatch.engine.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/12 14:07
 */
public final class VersionGenerator {

    /**
     * 生成集群版本
     *
     * @param serverList
     * @return
     */
    public static String version(List<String> serverList) {
        Collections.sort(serverList);
        String servers = StringUtils.join(serverList.toArray(new String[serverList.size()]), "_");
        return DigestUtils.md5Hex(servers);
    }
}
