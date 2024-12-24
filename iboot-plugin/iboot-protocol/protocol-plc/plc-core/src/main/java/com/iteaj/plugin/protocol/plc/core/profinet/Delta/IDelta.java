package com.iteaj.plugin.protocol.plc.core.profinet.Delta;

import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;

/**
 * 台达的系列信息
 */
public interface IDelta extends IReadWriteDevice {
    /**
     * 获取当前的台达PLC的系列信息，默认为 DVP 系列<br />
     * Get the current series information of Delta PLC, the default is DVP series
     * @return 台达的系列
     */
    DeltaSeries GetSeries();

    /**
     * 设置当前的台达PLC的系列信息，默认为 DVP 系列<br />
     * Set the current series information of Delta PLC, the default is DVP series
     * @param series 台达的系列
     */
    void SetSeries(DeltaSeries series);

}
