package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.AbstractClientProtocol;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

public abstract class PlcProtocolAbstract<M extends PlcClientMessage> extends AbstractClientProtocol<M> implements PlcReadWriteProtocol {

    /**
     * plc地址
     */
    private String address;

    /**
     * 执行结果
     */
    private OperateResult result;

    /**
     * plc协议
     */
    private PlcProtocolType protocolType;

    /**
     * 要操作的客户端
     */
    private PlcClientAdapter clientAdapter;

    private static ThreadLocal<PlcProtocolAbstract> threadLocal = new ThreadLocal<>();

    public PlcProtocolAbstract(PlcProtocolType protocolType, PlcClientAdapter client) {
        this.clientAdapter = client;
        this.protocolType = protocolType;
    }

    @Override
    public PlcClientAdapter getIotClient() throws ProtocolException {
        return clientAdapter;
    }

    public boolean isWrite() {
        return this.result.IsSuccess && result.getClass() == OperateResult.class;
    }

    public Object getValue() {
        if(this.result.IsSuccess && this.result instanceof OperateResultExOne) {
            return ((OperateResultExOne<?>) this.result).Content;
        } else {
            return null;
        }
    }

    public static PlcProtocolAbstract get() {
        return threadLocal.get();
    }

    public abstract M build(byte[] message);

    @Override
    public AbstractClientProtocol buildRequestMessage() {
        threadLocal.set(this);
        return this;
    }

    @Override
    public AbstractClientProtocol buildResponseMessage() {
        threadLocal.remove();
        if(this.result != null && !this.result.IsSuccess) {
            this.setReason(this.result.Message);
            this.setExecStatus(ExecStatus.fail);
            throw new PlcProtocolException(this.getReason());
        }

        return this;
    }

    @Override
    public PlcProtocolType protocolType() {
        return this.protocolType;
    }

    @Override
    public byte[] read(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<byte[]> read = getIotClient().Read(getAddress(), length);
            this.result = read;
            if(read.IsSuccess) {
                return read.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    public Byte readByte(String address) {
        this.setAddress(address);
        try {
            buildRequestMessage();
            this.result = doReadByte(getAddress());
            if(this.result.IsSuccess) {
                return ((OperateResultExOne<Byte>) this.result).Content;
            }
        } finally {
            buildResponseMessage();
        }

        return null;
    }

    protected OperateResultExOne<Byte> doReadByte(String address) {
        throw new UnsupportedOperationException(protocolType()+"不支持读字节操作");
    }

    public Date readDate(String address) {
        this.setAddress(address);
        try {
            buildRequestMessage();
            this.result = doReadDate(getAddress());
            if(this.result.IsSuccess) {
                return ((OperateResultExOne<Date>) this.result).Content;
            }

            return null;
        } finally {
            buildResponseMessage();
        }
    }

    protected OperateResultExOne<Date> doReadDate(String address) {
        throw new UnsupportedOperationException(protocolType()+"不支持读日期操作");
    }

    public Date readDatetime(String address) {
        this.setAddress(address);
        try {
            buildRequestMessage();
            this.result = doReadDatetime(getAddress());
            if(this.result.IsSuccess) {
                return ((OperateResultExOne<Date>) this.result).Content;
            }

            return null;
        } finally {
            buildResponseMessage();
        }
    }

    protected OperateResultExOne<Date> doReadDatetime(String address) {
        throw new UnsupportedOperationException(protocolType()+"不支持读日期操作");
    }

    public Date readDltDatetime(String address) {
        this.setAddress(address);
        try {
            buildRequestMessage();
            this.result = doReadDltDatetime(getAddress());
            if(this.result.IsSuccess) {
                return ((OperateResultExOne<Date>) this.result).Content;
            }

            return null;
        } finally {
            buildResponseMessage();
        }
    }

    protected OperateResultExOne<Date> doReadDltDatetime(String address) {
        throw new UnsupportedOperationException(protocolType()+"不支持读Dlt日期操作");
    }

    @Override
    public boolean[] readBool(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<boolean[]> result = getIotClient().ReadBool(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public short[] readInt16(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<short[]> result = getIotClient().ReadInt16(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public int[] readUInt16(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<int[]> result = getIotClient().ReadUInt16(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public int[] readInt32(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<int[]> result = getIotClient().ReadInt32(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public long[] readUInt32(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<long[]> result = getIotClient().ReadUInt32(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public long[] readInt64(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<long[]> result = getIotClient().ReadInt64(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public float[] readFloat(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<float[]> result = getIotClient().ReadFloat(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public double[] readDouble(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<double[]> result = getIotClient().ReadDouble(getAddress(), length);
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public String readString(String address, short length, String encoding) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<String> result = getIotClient().ReadString(getAddress(), length, Charset.forName(encoding));
            this.result = result;
            if(result.IsSuccess) {
                return result.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    public void writeByte(String address, byte value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = doWriteByte(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    private OperateResult doWriteByte(String address, byte value) {
        throw new UnsupportedOperationException(protocolType()+"不支持写字节操作");
    }

    public void writeDate(String address, Date date) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = doWriteDate(getAddress(), date);
        } finally {
            this.buildResponseMessage();
        }
    }

    protected OperateResult doWriteDate(String address, Date date) {
        throw new UnsupportedOperationException(protocolType()+"不支持写日期操作");
    }

    public void writeDltDatetime(String address, Date date) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = doWriteDltDatetime(getAddress(), date);
        } finally {
            this.buildResponseMessage();
        }
    }

    protected OperateResult doWriteDltDatetime(String address, Date date) {
        throw new UnsupportedOperationException(protocolType()+"不支持写Dlt日期操作");
    }

    @Override
    public void write(String address, byte[] value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, boolean[] value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, short value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, short[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, int value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, int[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, long value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, long[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, float value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, float[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, double value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, double[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, String value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, String value, String encoding) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(getAddress(), value, Charset.forName(encoding));
        } finally {
            this.buildResponseMessage();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.toUpperCase(Locale.ROOT) : null;
    }
}
