package com.iteaj.framework.spi.iot;

import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.IotClient;

public abstract class StaticClientProtocolSupplier<C extends NetworkConfigImpl, T extends ClientComponent, K extends IotClient>
        extends StaticProtocolSupplier<C, T> implements ClientProtocolSupplier<C, T, K>{

}
