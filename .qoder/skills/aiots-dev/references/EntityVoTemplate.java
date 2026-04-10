// ===== 移动端参数实体类示例=====
import lombok.Getter;
import lombok.Setter;
import com.iteaj.framework;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Xxx实体
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(description = "Xxx参数")
public class XxxVo {

// ===== 移动端要显示的模型字段, 减轻网络压力=====
    /** 名称 */
    @ApiModelProperty(description = "名称")
    private String name;

}
