package com.iteaj.framework.spi.iot;

import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.ServiceLoaderUtil;
import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协议提供管理
 */
public class ProtocolSupplierManager {

    private static ProtocolSupplierManager instance = new ProtocolSupplierManager();
    private static Logger logger = LoggerFactory.getLogger(ProtocolSupplierManager.class);
    private static Map<String, DeviceProtocolSupplier> supplierMap = new HashMap<>(8);

    private ProtocolSupplierManager() { }

    /**
     * 协议提供实例
     * @return
     */
    public static ProtocolSupplierManager getInstance() {
        return instance;
    }

    /**
     * 注册一个协议
     * @param supplier
     * @return
     */
    public static ProtocolSupplierManager register(DeviceProtocolSupplier supplier) {
        ProtocolModel protocol = supplier.getProtocol();
        if(supplier == null || protocol == null) {
            throw new FrameworkException("参数错误");
        }

        if(contain(protocol.getCode())) {
            throw new FrameworkException("协议已存在["+protocol.getCode()+"]");
        }

        supplierMap.put(protocol.getCode(), supplier);
        return instance;
    }

    /**
     * 从jar文件读取, 使用spi机制
     * @param jarFileOrDir jar文件或者jar文件目录
     * @return
     */
    public static List<DeviceProtocolSupplier> load(File jarFileOrDir) {
        List<DeviceProtocolSupplier> loads = new ArrayList<>();
        try {
            JarClassLoader jarClassLoader;
            if(jarFileOrDir.isDirectory()) {
                jarClassLoader = JarClassLoader.load(jarFileOrDir);
            } else {
                jarClassLoader = JarClassLoader.loadJar(jarFileOrDir);
            }

            ServiceLoaderUtil.load(DeviceProtocolSupplier.class, jarClassLoader).forEach(item -> {
                // 只加载上传的jar包, 即实现类型为Jar
                if(item.getImplMode() == ProtocolImplMode.Jar) {
                    loads.add(item);
                    register(item);
                }
            });
        } catch (FrameworkException se) {
            throw new ServiceException(se.getMessage(), se);
        } catch (Exception e) {
            throw new ServiceException("导入Jar失败", e);
        }

        return loads;
    }

    /**
     * 从指定的jar目录下面加载jar包 并解析出里面的协议提供
     * @param jarDir
     * @return
     */
    public static List<DeviceProtocolSupplier> load(String jarDir) {
        return load(new File(jarDir));
    }

    /**
     * 获取指定协议
     * @param code
     * @return
     */
    public static DeviceProtocolSupplier get(String code) {
        return supplierMap.get(code);
    }

    /**
     * 是否包含某个协议
     * @param code
     * @return
     */
    public static boolean contain(String code) {
        return supplierMap.containsKey(code);
    }

    /**
     * 移除指定协议
     * @param code
     * @return
     */
    public static DeviceProtocolSupplier remove(String code) {
        DeviceProtocolSupplier supplier = supplierMap.get(code);
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
