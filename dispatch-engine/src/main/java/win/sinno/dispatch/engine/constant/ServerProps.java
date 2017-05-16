package win.sinno.dispatch.engine.constant;

/**
 * handler group
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-05 11:38.
 */
public interface ServerProps {

    String HANDLER_GROUP = "server.handler.group";

    String HANDLERS = "server.handlers";

    String CORE_SIZE = "server.core.size";

    String MAX_SIZE = "server.max.size";

    String VIRTUAL_NODE_NUM = "server.virtual.node.num";

    String DIVIDE_TYPE = "server.divide.type";

    String MAX_TRYTIME = "server.max.trytime";

    String DISPATCH_SERVICE = "server.dispatch.service";

    String DISPATCH_HANDLER_CONVERTER = "server.dispatch.handler.converter";

    String SERVER_PER_FETCH_SLEEP_TIMEMS = "server.per.fetch.sleep.timems";

    String SERVER_PER_FETCH_NUM = "server.per.fetch.num";

    String DEFAULT_REGISTER_VERSION = "0";
}
