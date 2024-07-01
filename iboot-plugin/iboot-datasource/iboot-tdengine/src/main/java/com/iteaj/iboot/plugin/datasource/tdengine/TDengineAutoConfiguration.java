package com.iteaj.iboot.plugin.datasource.tdengine;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.iteaj.framework.ProfilesInclude;
import com.iteaj.iboot.plugin.datasource.tdengine.listener.TaosRealtimeDataListener;
import com.iteaj.iboot.plugin.datasource.tdengine.service.TaosDataService;
import com.iteaj.iot.taos.TaosSqlManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@ProfilesInclude("tdengine")
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
public class TDengineAutoConfiguration {

    private static final String DS_NAME = "tdengine";

    @Bean
    public Object taosDataSource(DynamicRoutingDataSource dataSource) {
        DataSource tdengine = dataSource.getDataSource(DS_NAME);
        if(tdengine instanceof ItemDataSource) {
            if(!((ItemDataSource) tdengine).getName().equals(DS_NAME)) {
                throw new BeanCreationException("没有找到动态数据源["+DS_NAME+"]");
            }
        }

        return tdengine;
    }

    @Bean
    public TaosRealtimeDataListener taosRealtimeDataListener(TaosSqlManager taosSqlManager) {
        return new TaosRealtimeDataListener(taosSqlManager);
    }

    @Bean
//    @Primary
    public TaosDataService taosDataService() {
        return new TaosDataService();
    }
}
