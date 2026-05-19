// ===== Mapper 接口示例 =====
// - 继承 BaseMapper<T>
// - 加 @Mapper 注解
// - 简单 CRUD 由 BaseMapper 提供，无需额外声明
// - 自定义复杂查询（如联表分页）在此声明方法，SQL 写在对应 XML 文件中

import com.iteaj.framework.entity.Xxx;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XxxMapper extends BaseMapper<Xxx> {

    XxxDto detailById(Serializable id);

    IPage<XxxDto> pageOfDetail(Page<Xxx> page, XxxDto dto);
}
