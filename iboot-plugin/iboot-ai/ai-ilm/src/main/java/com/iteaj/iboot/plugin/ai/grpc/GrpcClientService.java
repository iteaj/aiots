package com.iteaj.iboot.plugin.ai.grpc;

import com.iteaj.iboot.plugin.ai.AiProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * gRPC客户端服务
 * 封装与Python端gRPC服务的通信逻辑，仅传递task_id，Python端从数据库读取其余参数
 * @author aiots-dev
 */
@Slf4j
@Service
public class GrpcClientService {

    private final AiProperties aiProperties;
    private ManagedChannel channel;

    public GrpcClientService(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    /**
     * 获取或创建gRPC通道
     */
    private ManagedChannel getChannel() {
        if (channel == null || channel.isShutdown()) {
            channel = ManagedChannelBuilder
                    .forAddress(aiProperties.getGrpcHost(), aiProperties.getGrpcPort())
                    .usePlaintext()
                    .build();
            log.info("gRPC通道已创建: {}:{}", aiProperties.getGrpcHost(), aiProperties.getGrpcPort());
        }
        return channel;
    }

    /**
     * 构建TaskRequest
     */
    private TaskRequest buildRequest(Long taskId) {
        return TaskRequest.newBuilder().setTaskId(taskId).build();
    }

    /**
     * 启动训练任务
     * @param taskId 训练任务ID
     * @return 是否成功
     */
    public boolean startTrain(Long taskId) {
        try {
            AiTrainServiceGrpc.AiTrainServiceBlockingStub stub = AiTrainServiceGrpc.newBlockingStub(getChannel());
            TaskResponse response = stub.startTrain(buildRequest(taskId));

            log.info("启动训练任务gRPC调用: taskId={}, success={}, message={}",
                    taskId, response.getSuccess(), response.getMessage());
            return response.getSuccess();
        } catch (Exception e) {
            log.error("启动训练任务gRPC调用失败: taskId={}", taskId, e);
            throw new RuntimeException("启动训练任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 停止训练任务
     * @param taskId 训练任务ID
     * @return 是否成功
     */
    public boolean stopTrain(Long taskId) {
        try {
            AiTrainServiceGrpc.AiTrainServiceBlockingStub stub = AiTrainServiceGrpc.newBlockingStub(getChannel());
            TaskResponse response = stub.stopTrain(buildRequest(taskId));

            log.info("停止训练任务gRPC调用: taskId={}, success={}, message={}",
                    taskId, response.getSuccess(), response.getMessage());
            return response.getSuccess();
        } catch (Exception e) {
            log.error("停止训练任务gRPC调用失败: taskId={}", taskId, e);
            throw new RuntimeException("停止训练任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 启动测试任务
     * @param taskId 测试任务ID
     * @return 是否成功
     */
    public boolean startTest(Long taskId) {
        try {
            AiTestServiceGrpc.AiTestServiceBlockingStub stub = AiTestServiceGrpc.newBlockingStub(getChannel());
            TaskResponse response = stub.startTest(buildRequest(taskId));

            log.info("启动测试任务gRPC调用: taskId={}, success={}, message={}",
                    taskId, response.getSuccess(), response.getMessage());
            return response.getSuccess();
        } catch (Exception e) {
            log.error("启动测试任务gRPC调用失败: taskId={}", taskId, e);
            throw new RuntimeException("启动测试任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 停止测试任务
     * @param taskId 测试任务ID
     * @return 是否成功
     */
    public boolean stopTest(Long taskId) {
        try {
            AiTestServiceGrpc.AiTestServiceBlockingStub stub = AiTestServiceGrpc.newBlockingStub(getChannel());
            TaskResponse response = stub.stopTest(buildRequest(taskId));

            log.info("停止测试任务gRPC调用: taskId={}, success={}, message={}",
                    taskId, response.getSuccess(), response.getMessage());
            return response.getSuccess();
        } catch (Exception e) {
            log.error("停止测试任务gRPC调用失败: taskId={}", taskId, e);
            throw new RuntimeException("停止测试任务失败: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                log.info("gRPC通道已关闭");
            } catch (InterruptedException e) {
                log.warn("gRPC通道关闭异常", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
