package com.iteaj.iboot.plugin.ai.dto;

import com.iteaj.iboot.plugin.ai.entity.AiDataset;
import com.iteaj.iboot.plugin.ai.entity.AiDatasetItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * AI数据集 DTO数据模型
 *
 * 【说明】
 * 1. DTO用于扩展实体类，包含搜索条件、关联数据等额外字段
 * 2. 继承实体类，自动拥有实体类的所有字段
 *
 * 【表关联处理】
 * - 一对多关联：添加关联的实体列表字段（数据集明细列表）
 *
 * @author aiots-dev
 * @since 2026-04-18
 */
@Getter
@Setter
@Accessors(chain = true)
public class AiDatasetDto extends AiDataset {

    // ===== 额外的搜索条件 =====
    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    // ===== 一对多关联列表 =====
    /**
     * 关联的数据集明细列表(一对多)
     */
    private List<AiDatasetItem> items;
}
