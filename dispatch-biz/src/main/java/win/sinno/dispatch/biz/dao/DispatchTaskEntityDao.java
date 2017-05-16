package win.sinno.dispatch.biz.dao;

import org.apache.ibatis.annotations.Param;
import win.sinno.dao.IDao;
import win.sinno.dao.MybatisRepository;
import win.sinno.dispatch.biz.model.DispatchTaskEntity;

import java.util.List;

/**
 * dispatch task entity dao
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/11 10:48
 */
@MybatisRepository
public interface DispatchTaskEntityDao extends IDao<DispatchTaskEntity> {

    List<DispatchTaskEntity> selectWithLimit(@Param("handlerGroup") String handlerGroup, @Param("nodes") List<Integer> nodes, @Param("limit") Integer limit);

    Long updateStatusByIdAndHandlerGroup(@Param("id") Long taskId, @Param("handlerGroup") String handlerGroup, @Param("status") Integer status);

    Long updateStatusByBizIdAndHandlerGroup(@Param("bizUniqueId") String bizUniqueId, @Param("handlerGroup") String handlerGroup, @Param("status") Integer status);

    Long updateNextExecTimeAndIncrRetryTime(@Param("id") Long taskId, @Param("nextExecTime") Long nextExecTime);
}
