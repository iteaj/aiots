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
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import com.iteaj.framework.entity.Xxx;
import com.iteaj.framework.service.IXxxService;

/**
 * Xxx移动端接口
 * @author ${author}
 * @since ${date}
 */
@RestController
@RequestMapping("/m/xxx")
@Tag(tags = "Xxx移动端接口", description = "")
public class XxxController extends BaseController {

    private final IXxxService xxxService;

    public XxxController(IXxxService xxxService) {
        this.xxxService = xxxService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询列表")
    @GetMapping("/page")
    @Operation(summary = "分页查询列表", description = "根据查询条件分页查询列表")
    public Result<IPage<Xxx>> page(Page page, Xxx xxx) {
        return xxxService.page(page, xxx);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询")
    @GetMapping("/list")
    @Operation(summary = "列表查询", description = "根据查询条件查询列表")
    public Result<List<Xxx>> list(Xxx xxx) {
        return xxxService.list(xxx);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取详情记录")
    @GetMapping("/detail")
    @Operation(summary = "获取详情记录", description = "根据记录ID获取详情记录")
    public Result<Xxx> detail(Long id) {
        return this.xxxService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新记录")
    @PostMapping("/saveOrUpdate")
    @Operation(summary = "新增或者更新记录", description = "根据记录ID判断是新增还是更新")
    public Result<Boolean> saveOrUpdate(@RequestBody Xxx xxx) {
        return this.xxxService.saveOrUpdate(xxx);
    }

    /**
     * 删除
     */
    @Logger("删除")
    @PostMapping("/del")
    @Operation(summary = "删除", description = "根据记录ID删除记录")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.xxxService.removeByIds(ids);
    }
}

