package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.AbstractClientProtocol;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.nio.charset.Charset;

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
        if(!this.result.IsSuccess) {
            this.setReason(this.result.Message);
            this.setExecStatus(ExecStatus.fail);
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
            OperateResultExOne<byte[]> read = getIotClient().Read(address, length);
            this.result = read;
            if(read.IsSuccess) {
                return read.Content;
            }
        } finally {
            this.buildResponseMessage();
        }

        return null;
    }

    @Override
    public boolean[] readBool(String address, short length) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            OperateResultExOne<boolean[]> result = getIotClient().ReadBool(address, length);
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
            OperateResultExOne<short[]> result = getIotClient().ReadInt16(address, length);
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
            OperateResultExOne<int[]> result = getIotClient().ReadUInt16(address, length);
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
            OperateResultExOne<int[]> result = getIotClient().ReadInt32(address, length);
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
            OperateResultExOne<long[]> result = getIotClient().ReadUInt32(address, length);
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
            OperateResultExOne<long[]> result = getIotClient().ReadInt64(address, length);
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
            OperateResultExOne<float[]> result = getIotClient().ReadFloat(address, length);
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
            OperateResultExOne<double[]> result = getIotClient().ReadDouble(address, length);
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
            OperateResultExOne<String> result = getIotClient().ReadString(address, length, Charset.forName(encoding));
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
    public void write(String address, byte[] value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, boolean[] value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, short value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, short[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, int value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, int[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, long value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, long[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, float value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, float[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, double value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, double[] values) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, values);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, String value) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value);
        } finally {
            this.buildResponseMessage();
        }
    }

    @Override
    public void write(String address, String value, String encoding) {
        this.setAddress(address);
        try {
            this.buildRequestMessage();
            this.result = getIotClient().Write(address, value, Charset.forName(encoding));
        } finally {
            this.buildResponseMessage();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
