package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.AbstractClientProtocol;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.nio.charset.Charset;

public abstract class PlcProtocolAbstract<M extends PlcClientMessage> extends AbstractClientProtocol<M> implements PlcReadWriteProtocol {

    private Object connectKey;
    private PlcProtocolType protocolType;
    private PlcClientComponent component;

    public PlcProtocolAbstract(Object connectKey
            , PlcProtocolType protocolType, PlcClientComponent component) {
        this.component = component;
        this.connectKey = connectKey;
        this.protocolType = protocolType;
    }

    @Override
    public PlcClientAdapter getIotClient() throws ProtocolException {
        return (PlcClientAdapter) component.getClient(connectKey);
    }

    @Override
    public AbstractClientProtocol buildRequestMessage() {
        return this;
    }

    @Override
    public AbstractClientProtocol buildResponseMessage() {
        return this;
    }

    @Override
    public PlcProtocolType protocolType() {
        return this.protocolType;
    }

    @Override
    public byte[] read(String address, short length) {
        OperateResultExOne<byte[]> read = getIotClient().Read(address, length);
        if(read.IsSuccess) {
            return read.Content;
        } else {
            throw new PlcProtocolException(read.Message, this);
        }
    }

    @Override
    public boolean[] readBool(String address, short length) {
        OperateResultExOne<boolean[]> result = getIotClient().ReadBool(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public short[] readInt16(String address, short length) {
        OperateResultExOne<short[]> result = getIotClient().ReadInt16(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public int[] readUInt16(String address, short length) {
        OperateResultExOne<int[]> result = getIotClient().ReadUInt16(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public int[] readInt32(String address, short length) {
        OperateResultExOne<int[]> result = getIotClient().ReadInt32(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public long[] readUInt32(String address, short length) {
        OperateResultExOne<long[]> result = getIotClient().ReadUInt32(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public long[] readInt64(String address, short length) {
        OperateResultExOne<long[]> result = getIotClient().ReadInt64(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public float[] readFloat(String address, short length) {
        OperateResultExOne<float[]> result = getIotClient().ReadFloat(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public double[] readDouble(String address, short length) {
        OperateResultExOne<double[]> result = getIotClient().ReadDouble(address, length);
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public String readString(String address, short length, String encoding) {
        OperateResultExOne<String> result = getIotClient().ReadString(address, length, Charset.forName(encoding));
        if(result.IsSuccess) {
            return result.Content;
        } else {
            throw new PlcProtocolException(result.Message, this);
        }
    }

    @Override
    public void write(String address, byte[] value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, boolean[] value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, short value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, short[] values) {
        OperateResult write = getIotClient().Write(address, values);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, int value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, int[] values) {
        OperateResult write = getIotClient().Write(address, values);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, long value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, long[] values) {
        OperateResult write = getIotClient().Write(address, values);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, float value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, float[] values) {
        OperateResult write = getIotClient().Write(address, values);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, double value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, double[] values) {
        OperateResult write = getIotClient().Write(address, values);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, String value) {
        OperateResult write = getIotClient().Write(address, value);
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }

    @Override
    public void write(String address, String value, String encoding) {
        OperateResult write = getIotClient().Write(address, value, Charset.forName(encoding));
        if(!write.IsSuccess) {
            throw new PlcProtocolException(write.Message, this);
        }
    }
}
