package com.iteaj.iboot.plugin.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.plugin.ai.dto.AiTestTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTestTask;
import com.iteaj.iboot.plugin.ai.grpc.GrpcClientService;
import com.iteaj.iboot.plugin.ai.mapper.AiTestTaskMapper;
import com.iteaj.iboot.plugin.ai.service.IAiTestTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;

/**
 * AI模型测试任务服务实现
 * @author aiots-dev
 */
@Slf4j
@Service
public class AiTestTaskServiceImpl extends BaseServiceImpl<AiTestTaskMapper, AiTestTask> implements IAiTestTaskService {

    private final GrpcClientService grpcClientService;

    public AiTestTaskServiceImpl(GrpcClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @Override
    public DetailResult<AiTestTaskDto> detailById(Serializable id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    @Override
    public PageResult<IPage<AiTestTaskDto>> pageOfDetail(Page<AiTestTask> page, AiTestTaskDto dto) {
        return new PageResult<>(this.getBaseMapper().pageOfDetail(page, dto));
    }

    @Override
    public boolean toggle(AiTestTask task) {
        AiTestTask existing = this.getById(task.getId()).getData();
        if (existing == null) {
            throw new RuntimeException("测试任务不存在: " + task.getId());
        }

        String targetStatus = task.getStatus();
        if ("running".equals(targetStatus)) {
            // 启动测试：调用gRPC + 更新状态
            boolean grpcSuccess = grpcClientService.startTest(task.getId());
            if (!grpcSuccess) {
                throw new RuntimeException("gRPC调用启动测试失败");
            }
            existing.setStatus("running");
            existing.setStartTime(new Date());
        } else if ("cancelled".equals(targetStatus)) {
            // 取消测试：调用gRPC + 更新状态
            boolean grpcSuccess = grpcClientService.stopTest(task.getId());
            if (!grpcSuccess) {
                throw new RuntimeException("gRPC调用停止测试失败");
            }
            existing.setStatus("cancelled");
            existing.setEndTime(new Date());
        } else {
            existing.setStatus(targetStatus);
        }

        return this.updateById(existing).getData();
    }
}
