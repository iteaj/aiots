package com.iteaj.iboot.plugin.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.plugin.ai.dto.AiTrainTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTrainTask;

import java.io.Serializable;

/**
 * AI训练任务服务接口
 * @author aiots-dev
 */
public interface IAiTrainTaskService extends IBaseService<AiTrainTask> {

    /**
     * 根据ID查询训练任务详情（含模型名称、训练指标列表）
     * @param id 任务ID
     * @return 训练任务详情DTO
     */
    DetailResult<AiTrainTaskDto> detailById(Serializable id);

    /**
     * 分页查询训练任务列表（含模型名称）
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    PageResult<IPage<AiTrainTaskDto>> pageOfDetail(Page<AiTrainTask> page, AiTrainTaskDto dto);

    /**
     * 切换训练任务状态（开始/取消）
     * 通过gRPC与Python服务通信控制训练启停
     * @param task 包含id和目标status的任务对象
     * @return 是否成功
     */
    boolean toggle(AiTrainTask task);
}
