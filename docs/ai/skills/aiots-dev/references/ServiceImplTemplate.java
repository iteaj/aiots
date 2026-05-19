// ===== Service 实现类示例 =====
// - 继承 BaseServiceImpl<M, T>，M 为 Mapper 类型，T 为实体类型
// - 实现对应的 IXxxService 接口
// - 加 @Service 注解
// - 通过 getBaseMapper() 访问 Mapper

import com.iteaj.framework.entity.Xxx;
import com.iteaj.framework.mapper.XxxMapper;
import com.iteaj.framework.service.IXxxService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.iteaj.framework.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


/**
 * Xxx服务实现
 */
@Service
public class XxxServiceImpl extends BaseServiceImpl<XxxMapper, Xxx> implements IXxxService {

    /**
     * 根据id查询xxx详情记录
     * @param id 主键id
     * @return xxx详情记录
     */
    public DetailResult<XxxDto> detailById(Serializable id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    /**
     * 根据条件查询xxx列表分页详情记录
     * @param dto 查询条件
     * @return xxx分页记录
     */
    @Override
    public PageResult<IPage<XxxDto>> pageOfDetail(Page<Xxx> page, XxxDto dto) {
        return new PageResult<>(this.getBaseMapper().pageOfDetail(page, dto));
    }
}
