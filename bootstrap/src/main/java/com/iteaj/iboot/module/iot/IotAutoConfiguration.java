package com.iteaj.iboot.module.iot;

import com.iteaj.framework.spi.admin.Module;
import com.iteaj.iboot.module.iot.collect.store.RDBMStoreAction;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.listener.GatewayRestartListener;
import com.iteaj.iboot.module.iot.listener.ProductRestartListener;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IGatewayService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iot.tools.db.rdbms.DefaultRdbmsSqlManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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

    /**
     * 网关重启监听
     * @return
     */
    @Bean
    public GatewayRestartListener gatewayRestartListener(IGatewayService gatewayService) {
        return new GatewayRestartListener(gatewayService);
    }

    /**
     * 产品重启监听
     * @param productService
     * @param deviceService
     * @return
     */
    @Bean
    public ProductRestartListener productRestartListener(IProductService productService, IDeviceService deviceService) {
        return new ProductRestartListener(deviceService, productService);
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
}
