package com.iteaj.iboot.module.iot;

import com.iteaj.framework.spi.admin.Module;
import com.iteaj.framework.spring.condition.ConditionalOnCluster;
import com.iteaj.framework.spring.condition.ConditionalOnRedis;
import com.iteaj.iboot.module.iot.collect.data.HashMapRealtimeDataService;
import com.iteaj.iboot.module.iot.collect.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.collect.data.RedisRealtimeDataService;
import com.iteaj.iboot.module.iot.collect.store.RDBMStoreAction;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.collect.websocket.CollectDataRealtimeListener;
import com.iteaj.iot.tools.db.rdbms.DefaultRdbmsSqlManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.module.iot"})
@MapperScan({"com.iteaj.iboot.module.iot.mapper"})
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
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
    public RDBMStoreAction rdbmStoreAction() {
        return new RDBMStoreAction();
    }

    @Bean
    public DefaultRdbmsSqlManager rdbmsManager(DataSource dataSource) {
        return new DefaultRdbmsSqlManager(dataSource);
    }

    @Bean
    public StoreActionFactory storeActionFactory() {
        return StoreActionFactory.getInstance();
    }

    /**
     * 本地实时数据
     * @return
     */
    @Bean("realtimeDataService")
    @ConditionalOnMissingBean({RealtimeDataService.class})
    public RealtimeDataService localRealtimeDataService() {
        return new HashMapRealtimeDataService();
    }

    /**
     * redis实时数据
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnRedis
    @ConditionalOnCluster
    public RealtimeDataService redisRealtimeDataService() {
        return new RedisRealtimeDataService();
    }

    /**
     * websocket监听实时采集的数据
     * @return
     */
    @Bean
    public CollectDataRealtimeListener collectDataRealtimeListener() {
        return new CollectDataRealtimeListener();
    }
}
