// ===== 典型 Controller 示例 =====
// - 继承 BaseController
// - 构造器注入 Service
// - 使用 @Logger 记录操作日志
// - 返回值统一用 success() / fail()
// - 每个接口方法必须添加 Javadoc 注释说明接口用途

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import com.iteaj.framework.entity.Xxx;
import com.iteaj.framework.entity.XxxDto;
import com.iteaj.framework.service.IXxxService;

/**
 * 移动端Xxx功能
 * @author ${author}
 * @since ${date}
 */
@RestController
@RequestMapping("/m/xxx")
@Tag(tags = "Xxx功能", description = "移动端Xxx相关操作接口")
public class AppXxxController extends BaseController {

    private final IXxxService xxxService;

    public AppXxxController(IXxxService xxxService) {
        this.xxxService = xxxService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询列表")
    @GetMapping("/page")
    @Operation(summary = "分页查询列表", description = "根据查询条件分页查询列表")
    public Result<IPage<Xxx>> page(Page page, XxxDto xxx) {
        return this.xxxService.page(page, xxx); 
        // 当需要多表关联查询时使用 this.xxxService.pageOfDetail(page, xxx)
    }

    /**
     * 列表查询
     */
    @Logger("列表查询")
    @GetMapping("/list")
    @CheckPermission("module:resource:view")
    @Operation(summary = "列表查询", description = "根据查询条件查询列表")
    public Result<List<Xxx>> list(Xxx xxx) {
        return xxxService.list(xxx);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    @Operation(summary = "获取编辑记录", description = "根据编辑记录ID获取编辑记录")
    public Result<Xxx> edit(Long id) {
        return this.xxxService.getById(id);
    }


    /**
     * 获取记录详情
     */
    @Logger("获取记录详情")
    @GetMapping("/detail")
    @Operation(summary = "获取记录详情", description = "根据记录ID获取记录详情(包含关联数据)")
    public Result<XxxDto> detail(Long id) {
        return this.xxxService.detailById(id);
    }
}
