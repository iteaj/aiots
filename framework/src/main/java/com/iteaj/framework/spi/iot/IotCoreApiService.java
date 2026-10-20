package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.data.*;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class IotCoreApiService implements ApplicationReadyListener {

    private static ProductModelService productModelService;
    private static ModelAttrMetaService modelAttrMetaService;
    private static DataCollectPublishService dataCollectPublishService;
    private static IModelAttrRealtimeDataService modelAttrRealtimeDataService;

    private final static IotCoreApiService instance = new IotCoreApiService();

    private IotCoreApiService() { }

    public static IotCoreApiService newInstance() {
        return instance;
    }

    public static ModelAttrMeta getAttrMeta(Serializable productCode, Long modelAttrId) {
        return modelAttrMetaService.getAttrMeta(productCode, modelAttrId);
    }

    public static ModelAttrMeta getAttrMeta(Serializable productCode, String field) {
        return modelAttrMetaService.getAttrMetas(productCode).values()
                .stream()
                .filter(meta -> meta.getField().equals(field))
                .findFirst()
                .orElse(null);
    }

    public static Map<Serializable, ModelAttrMeta> getAttrMetas(Serializable productCode) {
        return modelAttrMetaService.getAttrMetas(productCode);
    }

    public static void publish(DataSupplier supplier, RealtimeDeviceStatus device) {
        dataCollectPublishService.publish(supplier, device);
    }

    public static void publish(DataSupplier supplier, RealtimeDeviceStatus device, String type) {
        dataCollectPublishService.publish(supplier, device, type);
    }

    /**
     * 根据产品Id获取产品信息
     * @param productId 产品Id
     * @return 产品信息
     */
    public static ProductMeta getProductByCache(Long productId) {
        return productModelService.getProductByCache(productId);
    }


    /**
     *
     * 获取产品模型
     * @param productCode 产品码
     * @return 产品模型
     */
    public static ProductModelService.ProductModel getProductModel(String productCode) {
        return productModelService.getProductModel(productCode);
    }

    /**
     *
     * @param uid 设备uid
     * @param signalOrField 物模型属性
     * @return 实时采集数据
     */
    public static ModelAttrRealtimeData getRealtimeData(String uid, String signalOrField) {
        return modelAttrRealtimeDataService.getRealtimeData(uid, signalOrField);
    }

    /**
     * 获取设备的实时状态
     * @param uid 设备uid
     * @return 设备实时状态数据
     */
    public static RealtimeDeviceStatus getDeviceStatus(String uid) {
        return modelAttrRealtimeDataService.getDeviceStatus(uid);
    }

    /**
     * 获取指定分组下面的设备列表
     * @param groupId 分组ID
     * @return 设备列表
     */
    public static Collection<? extends RealtimeDeviceStatus> listByGroupId(Long groupId) {
        return modelAttrRealtimeDataService.listByGroupId(groupId);
    }

    /**
     * 获取指定产品下面的设备列表
     * @param productCode 产品码
     * @return 设备列表
     */
    public static Collection<? extends RealtimeDeviceStatus> listByProductCode(String productCode) {
        return modelAttrRealtimeDataService.listByProductCode(productCode);
    }

    /**
     * 根据协议码和设备编号获取设备信息
     * @param protocolCode 协议码
     * @param deviceSn 设备编号
     * @return {@link  RealtimeDeviceStatus}
     */
    public static RealtimeDeviceStatus getDeviceStatus(String protocolCode, String deviceSn) {
        return modelAttrRealtimeDataService.getDeviceStatus(protocolCode, deviceSn);
    }

    /**
     *  获取指定的网关子设备信息
     * @param protocolCode 协议码
     * @param deviceSn 设备编号(子设备编号)
     * @param parentDeviceSn 父设备编号(网关设备编号)
     * @return {@link  RealtimeDeviceStatus}
     */
    public static RealtimeDeviceStatus getDeviceStatus(String protocolCode, String deviceSn, String parentDeviceSn) {
        return modelAttrRealtimeDataService.getDeviceStatus(protocolCode, deviceSn, parentDeviceSn);
    }

    @Override
    public void started(ApplicationContext context) {
        productModelService = context.getBean(ProductModelService.class);
        modelAttrMetaService = context.getBean(ModelAttrMetaService.class);
        dataCollectPublishService = context.getBean(DataCollectPublishService.class);
        modelAttrRealtimeDataService = context.getBean(IModelAttrRealtimeDataService.class);
    }

    /**
     * 该方法的执行顺序
     * @return 最小值, 该服务需要在所有监听器之前执行 {@code IotRestartListener}
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
