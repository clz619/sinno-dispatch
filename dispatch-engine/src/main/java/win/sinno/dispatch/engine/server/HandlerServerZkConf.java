package win.sinno.dispatch.engine.server;

/**
 * handler server config
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/15 10:52
 */
public class HandlerServerZkConf {

    private String zkAddress;

    private String zkNamespace;

    private String zkRootPath;

    private int zkSessionTimeoutMs = 30 * 1000;

    private int zkConnectionTimeoutMs = 15 * 1000;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getZkNamespace() {
        return zkNamespace;
    }

    public void setZkNamespace(String zkNamespace) {
        this.zkNamespace = zkNamespace;
    }

    public String getZkRootPath() {
        return zkRootPath;
    }

    public void setZkRootPath(String zkRootPath) {
        this.zkRootPath = zkRootPath;
    }

    public int getZkSessionTimeoutMs() {
        return zkSessionTimeoutMs;
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
    }

    public int getZkConnectionTimeoutMs() {
        return zkConnectionTimeoutMs;
    }

    public void setZkConnectionTimeoutMs(int zkConnectionTimeoutMs) {
        this.zkConnectionTimeoutMs = zkConnectionTimeoutMs;
    }

    @Override
    public String toString() {
        return "HandlerServerZkConf{" +
                "zkAddress='" + zkAddress + '\'' +
                ", zkNamespace='" + zkNamespace + '\'' +
                ", zkRootPath='" + zkRootPath + '\'' +
                ", zkSessionTimeoutMs=" + zkSessionTimeoutMs +
                ", zkConnectionTimeoutMs=" + zkConnectionTimeoutMs +
                '}';
    }
}
