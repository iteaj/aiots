package com.iteaj.iboot.plugin.datasource.rdbms;

import com.iteaj.iboot.plugin.datasource.rdbms.listener.RdbmsRealtimeDataListener;
import com.iteaj.iot.tools.db.rdbms.DefaultRdbmsSqlManager;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class RdbmsAutoConfiguration {

    @Bean
    public DefaultRdbmsSqlManager rdbmsManager(DataSource dataSource) {
        return new DefaultRdbmsSqlManager(dataSource);
    }

    @Bean
    public RdbmsRealtimeDataListener rdbmsRealtimeDataListener(DefaultRdbmsSqlManager rdbmsManager) {
        return new RdbmsRealtimeDataListener(rdbmsManager);
    }
}
