// ===== 普通实体示例（继承 BaseEntity）=====
import com.baomidou.mybatisplus.annotation.SqlCondition;  // 模糊搜索时需导入，注意从annotation包导入
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import com.iteaj.framework;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Xxx数据模型
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("xxx")
public class Xxx extends BaseEntity {

    /** 名称 */
    @TableField("name")
    private String name;

    // 非数据库字段必须标注 @TableField(exist = false)
}


// ===== 树形实体示例（继承 TreeEntity）=====
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 菜单实体
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_menu")
public class Menu extends TreeEntity {

    /** 菜单名称 */
    private String name;

    /** 父节点ID */
    private Long pid;

    @Override
    public Long getPid() {
        return pid;
    }
}
