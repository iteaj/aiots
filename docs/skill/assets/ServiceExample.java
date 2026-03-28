// ===== Service 接口示例 =====
// - 继承 IBaseService<T>，T 为实体类
// - IBaseService 已内置常用 CRUD 方法（save/updateById/removeByIds/page/list/getById 等），返回值均为 Result 子类
// - 只需声明业务特有的方法

import com.iteaj.framework.IBaseService;

/**
 * ${moduleName}服务接口
 */
public interface IXxxService extends IBaseService<Xxx> {
    
}
