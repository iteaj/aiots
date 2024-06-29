package com.iteaj.iboot.plugin.datasource;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.iteaj.framework.ProfilesInclude;
import com.iteaj.iboot.plugin.datasource.listener.TaosRealtimeDataListener;
import com.iteaj.iot.taos.TaosSqlManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@ProfilesInclude("tdengine")
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
public class TDengineAutoConfiguration {

    @Bean
    public Object taosDataSource(DynamicRoutingDataSource dataSource) {
        DataSource tdengine = dataSource.getDataSource("tdengine");
        if(tdengine == null) {
            throw new BeanCreationException("没有找到动态数据源[tdengine]");
        }

        return tdengine;
    }

    @Bean
    public TaosRealtimeDataListener taosRealtimeDataListener(TaosSqlManager taosSqlManager) {
        return new TaosRealtimeDataListener(taosSqlManager);
    }
}
