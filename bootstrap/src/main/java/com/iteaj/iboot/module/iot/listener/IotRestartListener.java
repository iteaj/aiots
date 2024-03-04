package com.iteaj.iboot.module.iot.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IGatewayService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.TcpSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * iot模块监听应用重启时的初始化
 */
@Component
public class IotRestartListener implements ApplicationReadyListener {

    private final IGatewayService gatewayService;
    private final IDeviceService deviceService;
    private final IProductService productService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public IotRestartListener(IGatewayService gatewayService, IDeviceService deviceService, IProductService productService) {
        this.gatewayService = gatewayService;
        this.deviceService = deviceService;
        this.productService = productService;
    }

    @Override
    public void started(ApplicationContext context) {
        // 初始化网关信息
        initGateway();

        // 初始化产品信息
        initProduct();
    }

    private void initGateway() {
        // 启用所有已经存在的网关
        this.gatewayService.list(Wrappers.<Gateway>lambdaQuery().eq(Gateway::getStatus, GatewayStatus.start)).forEach(item -> {
            try {
                this.gatewayService.switchStatus(item.getId(), GatewayStatus.start);
            } catch (Exception e) {
                logger.error("<<< 启动网关[{}]失败 {}", item.getName(), e);
                this.gatewayService.update(Wrappers.<Gateway>lambdaUpdate()
                        .eq(Gateway::getId, item.getId())
                        .set(Gateway::getReason, e.getMessage()));
            }
        });
    }

    protected void initProduct() {
        // 启动所有已经启用的设备
        productService.listOfDetail(new Product().setStatus(FuncStatus.enabled)).forEach(item -> {
            if(item.getDeviceType() == DeviceType.Gateway) { // 启用边缘网关下面所有的设备
                DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(item.getProtocolCode());
                if(supplier == null) {
                    logger.warn("<<< 启动产品[{}]失败, 未提供协议[code={}]", item.getName(), item.getProtocolCode());
                    return; // 保存产品启动信息
                }

                if(supplier instanceof ClientProtocolSupplier) {
                    // 先启动网关后才能启动产品
                    if(item.getGatewayStatus() == GatewayStatus.start) {
                        this.deviceService.listByProductId(item.getId()).forEach(device -> {
                            try {
                                NetworkConfig networkConfig = createClientConfig(supplier, device);
                                IotClient client = ((ClientProtocolSupplier) supplier).createClient(networkConfig);
                                resolveClientStatus(client);
                            } catch (Exception e) {
                                logger.error("<<< 启动产品[{}]下的设备[{}]失败", item.getName(), device.getName(), e);
                            }
                        });
                    }
                }
            }
        });
    }

    private void resolveClientStatus(IotClient client) {
        if(client instanceof TcpSocketClient) {
            if(!((TcpSocketClient) client).isConnect()) {
                client.connect();
            }
        }
    }

    private NetworkConfig createClientConfig(DeviceProtocolSupplier supplier, DeviceDto device) {
        TransportProtocol type = supplier.getProtocol().getType();
        if(type == TransportProtocol.TCP) {
            NetworkConfigImpl.TcpNetworkConfig config = new NetworkConfigImpl.TcpNetworkConfig();
            config.setHost(device.getIp());
            config.setPort(device.getPort());
            config.setDeviceSn(device.getDeviceSn());
            return config;
        }
        return null;
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10000;
    }
}
