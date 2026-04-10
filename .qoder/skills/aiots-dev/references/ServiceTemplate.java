// ===== Service 接口示例 =====
// - 继承 IBaseService<T>，T 为实体类
// - IBaseService 已内置常用 CRUD 方法（save/updateById/removeByIds/page/list/getById 等），返回值均为 Result 子类
// - 只需声明业务特有的方法
import com.iteaj.framework.entity.Xxx;
import com.iteaj.framework.IBaseService;

/**
 * Xxx服务接口
 */
public interface IXxxService extends IBaseService<Xxx> {
    
    /**
     * 根据id查询xxx详情记录
     * @param id 主键id
     * @return xxx详情记录
     */
    DetailResult<XxxDto> detailById(Serializable id);

    /**
     * 分页查询xxx历史记录
     * @param page 分页参数
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<IPage<XxxDto>> pageOfDetail(Page<Xxx> page, XxxDto dto);
}
