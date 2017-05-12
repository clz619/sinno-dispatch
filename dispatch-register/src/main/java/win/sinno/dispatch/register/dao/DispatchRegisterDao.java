package win.sinno.dispatch.register.dao;

import org.apache.ibatis.annotations.Param;
import win.sinno.dao.IDao;
import win.sinno.dao.MybatisRepository;
import win.sinno.dispatch.register.model.DispatchRegister;

import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 14:08
 */
@MybatisRepository
public interface DispatchRegisterDao extends IDao<DispatchRegister> {

    DispatchRegister selectLastRegister(String handlerGroup);

    DispatchRegister selectRegister(@Param("handlerGroup") String handlerGroup, @Param("hostname") String hostName);

    List<DispatchRegister> selectRegisters(@Param("handlerGroup") String handlerGroup);

    Long updateRegisterWithVersion(@Param("registerVersion") String destRegisterVersion,
                                   @Param("registerTime") long registerTime,
                                   @Param("nodes") String nodes,
                                   @Param("handlerGroup") String handlerGroup,
                                   @Param("hostname") String hostName,
                                   @Param("expectVersion") long expectVersion);

}
