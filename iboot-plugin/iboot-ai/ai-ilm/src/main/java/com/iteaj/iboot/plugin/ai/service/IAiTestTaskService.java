package com.iteaj.iboot.plugin.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.plugin.ai.dto.AiTestTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTestTask;

import java.io.Serializable;

/**
 * AI模型测试任务服务接口
 * @author aiots-dev
 */
public interface IAiTestTaskService extends IBaseService<AiTestTask> {

    /**
     * 根据ID查询测试任务详情（含训练任务名称）
     * @param id 任务ID
     * @return 测试任务详情DTO
     */
    DetailResult<AiTestTaskDto> detailById(Serializable id);

    /**
     * 分页查询测试任务列表（含训练任务名称）
     * @param page 分页参数
     * @param dto  查询条件
     * @return 分页结果
     */
    PageResult<IPage<AiTestTaskDto>> pageOfDetail(Page<AiTestTask> page, AiTestTaskDto dto);

    /**
     * 切换测试任务状态（开始/取消）
     * 通过gRPC与Python服务通信控制测试启停
     * @param task 包含id和目标status的任务对象
     * @return 是否成功
     */
    boolean toggle(AiTestTask task);
}
