package com.iteaj.plugin.protocol.plc.core.profinet.Delta;

import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusTcpNet;
import com.iteaj.plugin.protocol.plc.core.profinet.Delta.Helper.DeltaHelper;
import com.iteaj.plugin.protocol.plc.core.types.FunctionOperateExTwo;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 台达PLC的网口通讯类，基于Modbus-Rtu协议开发，按照台达的地址进行实现。<br />
 * The tcp communication class of Delta PLC is developed based on the Modbus-Tcp protocol and implemented according to Delta's address.
 *
 * 适用于DVP-ES/EX/EC/SS型号，DVP-SA/SC/SX/EH型号，地址参考API文档，同时地址可以携带站号信息，举例：[s=2;D100],[s=3;M100]，可以动态修改当前报文的站号信息。<br />
 */
public class DeltaTcpNet extends ModbusTcpNet implements IDelta {
    public DeltaTcpNet() {
        super();
        getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public DeltaTcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    private DeltaSeries deltaSeries = DeltaSeries.Dvp;

    public DeltaSeries GetSeries() {
        return deltaSeries;
    }
    public void SetSeries(DeltaSeries series){
        this.deltaSeries = series;
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return DeltaHelper.TranslateToModbusAddress(this, address, modbusCode);
    }

    /**
     * @apiNote 地址支持X,Y,M,SM,S,T,C,HC，其中X和Y地址使用DD.DD格式，范围 X0.0~X63.15, Y0.0~Y63.15，其中X地址使用的是02功能码，其余的都是01功能码。<br />
     * Address supports X, Y, M, SM, S, T, C, HC, where X and Y addresses use the DD.DD format, and the range is X0.0~X63.15, Y0.0~Y63.15,
     * where X addresses are used The one is 02 function code, and the rest are 01 function code.
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return DeltaHelper.ReadBool( this, new FunctionOperateExTwo<String, Short, OperateResultExOne<boolean[]>>(){
            @Override
            public OperateResultExOne<boolean[]> Action(String content1, Short content2) {
                return DeltaTcpNet.super.ReadBool(content1, content2);
            }
        }, address, length );
    }

    /**
     * @apiNote 地址支持Y,M,SM,S,T,C,HC，其中Y地址使用DD.DD格式，Y0.0~Y63.15，不支持X地址的写入。<br />
     * The address supports Y, M, SM, S, T, C, HC, where the Y address uses the DD.DD format, Y0.0~Y63.15, the writing of the X address is not supported.
     */
    public OperateResult Write(String address, boolean[] values ) {
        return DeltaHelper.Write( this, new FunctionOperateExTwo<String, boolean[], OperateResult>(){
            @Override
            public OperateResult Action(String content1, boolean[] content2) {
                return DeltaTcpNet.super.Write(content1, content2);
            }
        }, address, values );
    }

    /**
     * @apiNote 地址支持Y,M,SM,S,T,C,HC，其中Y地址使用DD.DD格式，Y0.0~Y63.15，不支持X地址的写入。<br />
     * The address supports Y, M, SM, S, T, C, HC, where the Y address uses the DD.DD format, Y0.0~Y63.15, the writing of the X address is not supported.
     */
    public OperateResult Write(String address, boolean value ) {
        return super.Write( address, value );
    }

    /**
     * @apiNote 字地址支持X,Y,SR,D,T,C,HC,E, 所有的地址都是十进制的方式，地址范围参照API文档事例，举例：X1,Y10,SR100,D10,T20,C20,HC200,E2<br />
     * Word address supports X, Y, SR, D, T, C, HC, E, all addresses are in decimal format, and the address range refers to the API document example,
     * for example: X1, Y10, SR100, D10, T20, C20, HC200 ,E2
     */
    public OperateResultExOne<byte[]> Read( String address, short length ) {
        return DeltaHelper.Read( this, new FunctionOperateExTwo<String, Short, OperateResultExOne<byte[]>>(){
            @Override
            public OperateResultExOne<byte[]> Action(String content1, Short content2) {
                return DeltaTcpNet.super.Read(content1, content2);
            }
        }, address, length );
    }

    /**
     * @apiNote 字地址支持Y,SR,D,T,C,HC,E, 所有的地址都是十进制的方式，地址范围参照API文档事例，举例：Y10,SR100,D10,T20,C20,HC200,E2<br />
     * Word address supports Y, SR, D, T, C, HC, E, all addresses are in decimal format, and the address range refers to the API document example,
     * for example: Y10, SR100, D10, T20, C20, HC200 ,E2
     */
    public OperateResult Write( String address, byte[] value ) {
        return DeltaHelper.Write(this, new FunctionOperateExTwo<String, byte[], OperateResult>() {
            @Override
            public OperateResult Action(String content1, byte[] content2) {
                return DeltaTcpNet.super.Write(content1, content2);
            }
        }, address, value);
    }



    @Override
    public String toString() {
        return "DeltaTcpNet[" + getIpAddress() + ":" + getPort() + "]";
    }
}
