package win.sinno.dispatch.reigster.dao;

import win.sinno.dispatch.reigster.model.DispatchRegister;

import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 14:08
 */
public interface DispatchRegisterDao {

    long insert(DispatchRegister dispatchRegister);

    int updateRegisterWithVersion(String destRegisterVersion, long registerTime,
                                  String nodes, String handlerGroup, String hostName, long expectVersion);

    DispatchRegister selectLastRegister(String handlerGroup);

    DispatchRegister selectRegister(String handlerGroup, String hostName);

    List<DispatchRegister> selectRegisters(String handlerGroup);
}
