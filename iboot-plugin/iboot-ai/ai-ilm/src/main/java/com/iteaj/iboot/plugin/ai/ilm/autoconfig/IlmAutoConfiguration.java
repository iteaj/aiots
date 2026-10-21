package com.iteaj.iboot.plugin.ai.ilm.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 工业AI模块(ai-ilm)自动配置
 *
 * 负责装配工业视觉相关的AI能力(视频分析、图像识别等), 模块开关见 framework.ilm.enabled
 * 模块参数配置见 classpath 下的 application-ilm.properties (profile: ilm)
 *
 * 业务代码约定按以下包组织, 随功能逐步新增:
 *   1. controller: 管理端接口
 *   2. service: 业务逻辑实现
 *   3. entity/mapper: 识别任务等数据模型, 需要时再补 @MapperScan
 *
 * 注意: 扫描时排除 autoconfig 包, 避免配置类被重复注册
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "framework.ilm", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.iteaj.iboot.plugin.ai.ilm",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                pattern = "com\\.iteaj\\.iboot\\.plugin\\.ai\\.ilm\\.autoconfig\\..*"))
public class IlmAutoConfiguration {

}
