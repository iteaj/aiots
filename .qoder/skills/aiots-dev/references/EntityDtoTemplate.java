package com.iteaj.iboot.module.iot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Date;

/**
 * xxx记录 DTO数据模型
 * @author iteaj
 * @since ${date}
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "xxx记录 DTO数据模型")
public class XxxDto extends Xxx {


// ===== 额外的搜索条件 =====
    /**
     * 开始时间
     */
    @Schema(description = "xxx记录创建时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "xxx记录结束时间")
    private Date endTime;
}