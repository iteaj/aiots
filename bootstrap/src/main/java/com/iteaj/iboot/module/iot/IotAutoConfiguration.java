package com.iteaj.iboot.module.iot;

import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spring.condition.ConditionalOnCluster;
import com.iteaj.framework.spring.condition.ConditionalOnRedis;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.IotLocalCacheManager;
import com.iteaj.iboot.module.iot.cache.data.HashMapRealtimeDataService;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataCacheListener;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.data.RedisRealtimeDataService;
import com.iteaj.iboot.module.iot.collect.CollectDataListener;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.collect.model.DeviceStatusModelApiManager;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.collect.websocket.RealtimePushListener;
import com.iteaj.iboot.module.iot.linkage.*;
import com.iteaj.iboot.module.iot.linkage.warn.RDBMSPersistenceWarnListener;
import com.iteaj.iboot.module.iot.listener.PluginRealtimeInfoListenerPublisher;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.module.iot"})
@MapperScan({"com.iteaj.iboot.module.iot.mapper"})
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
@EnableConfigurationProperties(IotMsnProperties.class)
@ImportAutoConfiguration(IotProtocolSupplierAutoConfiguration.class)
public class IotAutoConfiguration {

    /**
     * 物联网模块
     * @return
     */
    @Bean
    public Module iotModule() {
        return Module.module("iot", "物联网模块", 55555);
    }

    @Bean
    public StoreActionFactory storeActionFactory() {
        return StoreActionFactory.getInstance();
    }

    @Bean
    public IotCacheManager iotLocalCacheManager() {
        return new IotLocalCacheManager();
    }

    /**
     * 告警数据实时入库
     * @param warnRecordService
     * @return
     */
    @Bean
    public RDBMSPersistenceWarnListener rdbmsPersistenceWarnListener(IWarnRecordService warnRecordService) {
        return new RDBMSPersistenceWarnListener(warnRecordService);
    }

    /**
     * 实时数据缓存处理
     * @param realtimeDataService
     * @return
     */
    @Bean
    public RealtimeDataCacheListener realtimeDataStoreListener(RealtimeDataService realtimeDataService) {
        return new RealtimeDataCacheListener(realtimeDataService);
    }

    /**
     * 将系统的协议实时数据(采集数据、设备上下线、告警)向各个插件模块发布
     * @return
     */
    @Bean
    public PluginRealtimeInfoListenerPublisher pluginRealtimeInfoListenerPublisher() {
        return new PluginRealtimeInfoListenerPublisher();
    }

    @Bean
    public CollectListenerManager eventGroupCollectManager(@Autowired(required = false) List<CollectDataListener> listeners) {
        return CollectListenerManager.build(listeners);
    }

    /**
     * 设备状态采集
     * @return
     */
    @Bean
    public DeviceStatusModelApiManager deviceStatusModelApiManager() {
        return DeviceStatusModelApiManager.getInstance();
    }

    /**
     * redis实时数据
     * @return
     */
    @Bean
    @ConditionalOnRedis
    @ConditionalOnCluster
    public RealtimeDataService redisRealtimeDataService() {
        return new RedisRealtimeDataService();
    }

    /**
     * 本地实时数据
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({RealtimeDataService.class})
    public RealtimeDataService localRealtimeDataService(IotCacheManager cacheManager) {
        return new HashMapRealtimeDataService(cacheManager);
    }

    /**
     * websocket监听实时采集的数据
     * @return
     */
    @Bean
    public RealtimePushListener collectDataRealtimeListener(RealtimeDataService realtimeDataService) {
        return new RealtimePushListener(realtimeDataService);
    }

    /**
     * 本地实现的联动项缓存服务
     * @return
     */
    @Bean
    public LinkageCacheService localLinkageCacheService() {
        return new LocalLinkageCacheService();
    }

    /**
     * 场景联动触发服务
     * @param cacheManager
     * @param realtimeDataService
     * @param linkageCacheService
     * @return
     */
    @Bean
    public LinkageTriggerService linkageTriggerService(IotCacheManager cacheManager
            , RealtimeDataService realtimeDataService, LinkageCacheService linkageCacheService) {
        return new LinkageTriggerService(cacheManager, realtimeDataService, linkageCacheService);
    }

    /**
     * 条件表达式运算符管理
     * @param calcs
     * @return
     */
    @Bean
    public ConditionalsManager conditionalsManager(@Autowired(required = false) List<ConditionCalc> calcs) {
        return ConditionalsManager.build(calcs);
    }

}
