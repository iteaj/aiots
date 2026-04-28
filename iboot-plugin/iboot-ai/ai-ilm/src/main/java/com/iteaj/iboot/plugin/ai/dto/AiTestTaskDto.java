package com.iteaj.iboot.plugin.ai.dto;

import com.iteaj.iboot.plugin.ai.entity.AiTestTask;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AI测试任务 DTO数据模型
 *
 * 【表关联】
 * - 多对一：ai_test_task → ai_train_task (train_task_id)，携带训练任务名称
 *
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
public class AiTestTaskDto extends AiTestTask {

    /** 训练任务名称（多对一关联 ai_train_task） */
    private String trainTaskName;
}
