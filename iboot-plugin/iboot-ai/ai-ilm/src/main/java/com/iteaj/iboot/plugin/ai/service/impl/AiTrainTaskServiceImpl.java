package com.iteaj.iboot.plugin.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.plugin.ai.dto.AiTrainTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTrainTask;
import com.iteaj.iboot.plugin.ai.grpc.GrpcClientService;
import com.iteaj.iboot.plugin.ai.mapper.AiTrainTaskMapper;
import com.iteaj.iboot.plugin.ai.service.IAiTrainTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;

/**
 * AI训练任务服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiTrainTaskServiceImpl extends BaseServiceImpl<AiTrainTaskMapper, AiTrainTask> implements IAiTrainTaskService {

    private final GrpcClientService grpcClientService;

    public AiTrainTaskServiceImpl(GrpcClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @Override
    public DetailResult<AiTrainTaskDto> detailById(Serializable id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    @Override
    public PageResult<IPage<AiTrainTaskDto>> pageOfDetail(Page<AiTrainTask> page, AiTrainTaskDto dto) {
        return new PageResult<>(this.getBaseMapper().pageOfDetail(page, dto));
    }

    @Override
    public boolean toggle(AiTrainTask task) {
        AiTrainTask existing = this.getById(task.getId()).getData();
        if (existing == null) {
            throw new RuntimeException("训练任务不存在: " + task.getId());
        }

        String targetStatus = task.getStatus();
        if ("running".equals(targetStatus)) {
            // 启动训练：调用gRPC + 更新状态
            boolean grpcSuccess = grpcClientService.startTrain(task.getId());
            if (!grpcSuccess) {
                throw new RuntimeException("gRPC调用启动训练失败");
            }
            existing.setStatus("running");
            existing.setStartTime(new Date());
        } else if ("cancelled".equals(targetStatus)) {
            // 取消训练：调用gRPC + 更新状态
            boolean grpcSuccess = grpcClientService.stopTrain(task.getId());
            if (!grpcSuccess) {
                throw new RuntimeException("gRPC调用停止训练失败");
            }
            existing.setStatus("cancelled");
            existing.setEndTime(new Date());
        } else {
            existing.setStatus(targetStatus);
        }

        return this.updateById(existing).getData();
    }
}
