// ===== 典型 Controller 示例 =====
// - 继承 BaseController
// - 构造器注入 Service
// - 使用 @Logger 记录操作日志
// - 使用 @CheckPermission 控制权限
// - 返回值统一用 success() / fail()
// - 每个接口方法必须添加 Javadoc 注释说明接口用途

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${moduleName}管理
 * @author ${author}
 * @since ${date}
 */
@RestController
@RequestMapping("/module/resource")
public class XxxController extends BaseController {

    private final IXxxService xxxService;

    public XxxController(IXxxService xxxService) {
        this.xxxService = xxxService;
    }

    /**
     * 分页查询列表
     */
    @Logger("查询列表")
    @GetMapping("/view")
    @CheckPermission("module:resource:view")
    public Result<IPage<Xxx>> view(Page page, Xxx xxx) {
        return success(xxxService.page(page, xxx));
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新记录")
    @PostMapping("/add")
    @CheckPermission("module:resource:add")
    public Result<Xxx> saveOrUpdate(@RequestBody Xxx xxx) {
        return success(this.xxxService.saveOrUpdate(xxx));
    }

    /**
     * 删除
     */
    @Logger("删除")
    @PostMapping("/del")
    @CheckPermission("module:resource:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.xxxService.removeByIds(ids);
    }
}
