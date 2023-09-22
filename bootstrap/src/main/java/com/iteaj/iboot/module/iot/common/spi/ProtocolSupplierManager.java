package com.iteaj.iboot.module.iot.common.spi;

import cn.hutool.core.lang.JarClassLoader;
import com.iteaj.iboot.module.iot.common.protocol.ProtocolModel;
import com.iteaj.iboot.module.iot.consts.ProtocolImplMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议提供管理
 */
public class ProtocolSupplierManager {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static ProtocolSupplierManager instance = new ProtocolSupplierManager();
    private Map<String, DeviceProtocolSupplier> supplierMap = new HashMap<>(8);

    static {
        instance = new ProtocolSupplierManager();
    }

    private ProtocolSupplierManager() { }

    /**
     * 协议提供实例
     * @return
     */
    public static ProtocolSupplierManager getInstance() {
        return instance;
    }

    public ProtocolSupplierManager put(DeviceProtocolSupplier supplier) {
        ProtocolModel protocol = supplier.getProtocol();
        if(supplier == null || protocol == null) {
            throw new IllegalArgumentException("参数错误");
        }

        supplierMap.put(protocol.getCode(), supplier);
        return this;
    }

    /**
     * 从jar文件读取, 使用spi机制
     * @param jarFile
     * @return
     */
    public DeviceProtocolSupplier load(String jarFile) {
        ClassLoader classLoader = getClass().getClassLoader();
        return null;
    }

    public DeviceProtocolSupplier load(InputStream inputStream) {
        ClassLoader classLoader = getClass().getClassLoader();
        return null;
    }

    /**
     * 获取指定协议
     * @param code
     * @return
     */
    public DeviceProtocolSupplier get(String code) {
        return supplierMap.get(code);
    }

    /**
     * 是否包含某个协议
     * @param code
     * @return
     */
    public boolean contain(String code) {
        return supplierMap.containsKey(code);
    }

    /**
     * 移除指定协议
     * @param code
     * @return
     */
    public DeviceProtocolSupplier remove(String code) {
        DeviceProtocolSupplier supplier = this.get(code);
        if(supplier != null) {
            // 系统内置不允许删除
            if(supplier.getImplMode() == ProtocolImplMode.Internal) {
                logger.warn("系统内置协议[{}]不允许删除", code);
                return null;
            }

            return supplierMap.remove(code);
        }

        return null;
    }
}
