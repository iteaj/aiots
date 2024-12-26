package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.util.Date;

public class SiemensS7Protocol extends PlcProtocolAbstract<SiemensMessage> {

    public SiemensS7Protocol(PlcClientAdapter client) {
        super(PlcProtocolType.Siemens, client);
    }

    @Override
    public SiemensMessage build(byte[] message) {
        return new SiemensMessage(message);
    }

    @Override
    public Byte readByte(String address) {
        getSiemensS7Net().ReadByte(address);
        return super.readByte(address);
    }

    protected SiemensS7Net getSiemensS7Net() {
        return (SiemensS7Net) getIotClient().getReadWriteNet();
    }

    @Override
    protected OperateResultExOne<Byte> doReadByte(String address) {
        return getSiemensS7Net().ReadByte(address);
    }

    @Override
    protected OperateResultExOne<Date> doReadDate(String address) {
        return getSiemensS7Net().ReadDate(address);
    }

    @Override
    protected OperateResultExOne<Date> doReadDatetime(String address) {
        return getSiemensS7Net().ReadDateTime(address);
    }

    @Override
    protected OperateResultExOne<Date> doReadDltDatetime(String address) {
        return getSiemensS7Net().ReadDTLDataTime(address);
    }

    @Override
    protected OperateResult doWriteDate(String address, Date date) {
        return getSiemensS7Net().WriteDate(address, date);
    }

    @Override
    protected OperateResult doWriteDltDatetime(String address, Date date) {
        return getSiemensS7Net().WriteDTLTime(address, date);
    }
}
