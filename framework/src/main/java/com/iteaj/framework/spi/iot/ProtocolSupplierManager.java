package com.iteaj.framework.spi.iot;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.ServiceLoaderUtil;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolSupplierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

/**
 * 协议提供管理
 */
public class ProtocolSupplierManager {

    private static FrameworkProperties properties;
    public static final String PROTOCOL_JAR_DIR = "protocol";
    public static final String PROTOCOL_JAR_TEMP_DIR = "protocol_temp";
    private static ProtocolSupplierManager instance = new ProtocolSupplierManager();
    private static Logger logger = LoggerFactory.getLogger(ProtocolSupplierManager.class);
    private static Map<String, DeviceProtocolSupplier> supplierMap = new HashMap<>(8);

    private ProtocolSupplierManager() { }

    public static ProtocolSupplierManager build(FrameworkProperties properties) {
        ProtocolSupplierManager.properties = properties; return instance;
    }

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
        return load(jarFileOrDir, false);
    }

    /**
     * 从jar文件读取, 使用spi机制
     * @param isClose 是否关闭类加载器
     * @param jarFileOrDir jar文件或者jar文件目录
     * @return
     */
    public static List<DeviceProtocolSupplier> load(File jarFileOrDir, boolean isClose) {
        List<DeviceProtocolSupplier> loads = new ArrayList<>();
        if(!jarFileOrDir.exists()) { // 文件不存在
            return Collections.emptyList();
        }

        JarClassLoader jarClassLoader = null;
        try {
            if(jarFileOrDir.isDirectory()) {
                jarClassLoader = JarClassLoader.load(jarFileOrDir);
            } else {
                jarClassLoader = JarClassLoader.loadJar(jarFileOrDir);
            }

            ServiceLoaderUtil.load(DeviceProtocolSupplier.class, jarClassLoader).forEach(item -> {
                // 只加载上传的jar包, 即实现类型为Jar
                if(item.getImplMode() == ProtocolImplMode.Jar) {
                    loads.add(item);
                }
            });
        } catch (FrameworkException se) {
            throw new ServiceException(se.getMessage(), se);
        } catch (Exception e) {
            throw new ServiceException("导入Jar失败", e);
        } finally {
            if(jarClassLoader != null && isClose) {
                try {
                    jarClassLoader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
     * 加载协议目录下的所有jar
     * @return
     */
    public static List<DeviceProtocolSupplier> loadFromProtocol() {
        String location = properties.getWeb().getUpload().getLocationPath();
        return load(location + File.separator + PROTOCOL_JAR_DIR);
    }

    /**
     * 从协议目录加载指定jar文件
     * @param fileName
     * @return
     */
    public static List<DeviceProtocolSupplier> loadFromProtocol(String fileName) {
        String location = properties.getWeb().getUpload().getLocationPath();
        File file = new File(location + File.separator + PROTOCOL_JAR_DIR + File.separator + fileName);
        return load(file);
    }

    /**
     * 从协议临时目录加载指定jar文件
     * @param fileName
     * @return
     */
    public static List<DeviceProtocolSupplier> loadFromTempProtocol(String fileName) {
        String location = properties.getWeb().getUpload().getLocationPath();
        File file = new File(location + File.separator + PROTOCOL_JAR_TEMP_DIR + File.separator + fileName);
        try {
            JarFile jarFile = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return load(file, true);
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

    public static DeviceProtocolSupplier moveTempToProtocol(String fileName) {
        String location = properties.getWeb().getUpload().getLocationPath();
        File target = new File(location + File.separator + PROTOCOL_JAR_DIR + File.separator + fileName);
        File temp = new File(location + File.separator + PROTOCOL_JAR_TEMP_DIR + File.separator + fileName);
        if(!temp.exists()) {
            throw new ProtocolSupplierException("Jar文件不存在["+fileName+"]");
        }

        FileUtil.move(temp, target, true);
        return load(target).get(0);
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
            ClassLoader classLoader = supplier.getClass().getClassLoader();
            if(classLoader instanceof JarClassLoader) {
                try {
                    ((JarClassLoader) classLoader).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return supplierMap.remove(code);
        }

        return null;
    }
}
