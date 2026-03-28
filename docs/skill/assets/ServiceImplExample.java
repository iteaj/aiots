// ===== Service 实现类示例 =====
// - 继承 BaseServiceImpl<M, T>，M 为 Mapper 类型，T 为实体类型
// - 实现对应的 IXxxService 接口
// - 加 @Service 注解
// - 通过 getBaseMapper() 访问 Mapper

import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * ${moduleName}服务实现
 */
@Service
public class XxxServiceImpl extends BaseServiceImpl<XxxMapper, Xxx> implements IXxxService {

}
