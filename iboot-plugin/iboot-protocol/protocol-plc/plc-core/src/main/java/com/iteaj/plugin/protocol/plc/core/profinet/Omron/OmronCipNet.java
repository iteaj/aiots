package com.iteaj.plugin.protocol.plc.core.profinet.Omron;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley.AllenBradleyHelper;
import com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley.AllenBradleyNet;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.Charset;

/**
 * 欧姆龙PLC的CIP协议的类，支持NJ,NX,NY系列PLC，支持tag名的方式读写数据，假设你读取的是局部变量，那么使用 Program:MainProgram.变量名<br />
 * Omron PLC's CIP protocol class, support NJ, NX, NY series PLC, support tag name read and write data, assuming you read local variables, then use Program: MainProgram.Variable name
 */
public class OmronCipNet extends AllenBradleyNet {
    /**
     * Instantiate a communication object for a OmronCipNet PLC protocol
     */
    public OmronCipNet( ) {
        super();
    }

    /**
     * Specify the IP address and port to instantiate a communication object for a OmronCipNet PLC protocol
     * @param ipAddress PLC IpAddress
     * @param port PLC Port
     */
    public OmronCipNet( String ipAddress, int port ) {
        super(ipAddress, port);
    }

    public OperateResultExOne<byte[]> Read(String address, short length )
    {
        if (length > 1)
            return Read( new String[] { address }, new int[] { 1 } );
        else
            return Read( new String[] { address }, new int[] { length } );
    }

    public OperateResultExOne<short[]> ReadInt16( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], short[]>() {
                @Override
                public short[] Action(byte[] content) {
                    return getByteTransform().TransInt16(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], short[]>() {
            @Override
            public short[] Action(byte[] content) {
                return getByteTransform().TransInt16(content, startIndexSecond < 0 ? 0 : startIndexSecond * 2, length);
            }
        });
    }

    public OperateResultExOne<int[]> ReadUInt16( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], int[]>() {
                @Override
                public int[] Action(byte[] content) {
                    return getByteTransform().TransUInt16(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransUInt16(content, startIndexSecond < 0 ? 0 : startIndexSecond * 2, length);
            }
        });
    }

    public OperateResultExOne<int[]> ReadInt32( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], int[]>() {
                @Override
                public int[] Action(byte[] content) {
                    return getByteTransform().TransInt32(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransInt32(content, startIndexSecond < 0 ? 0 : startIndexSecond * 4, length);
            }
        });
    }

    public OperateResultExOne<long[]> ReadUInt32( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], long[]>() {
                @Override
                public long[] Action(byte[] content) {
                    return getByteTransform().TransUInt32(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransUInt32(content, startIndexSecond < 0 ? 0 : startIndexSecond * 4, length);
            }
        });
    }

    public OperateResultExOne<float[]> ReadFloat( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], float[]>() {
                @Override
                public float[] Action(byte[] content) {
                    return getByteTransform().TransSingle(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], float[]>() {
            @Override
            public float[] Action(byte[] content) {
                return getByteTransform().TransSingle(content, startIndexSecond < 0 ? 0 : startIndexSecond * 4, length);
            }
        });
    }

    public OperateResultExOne<long[]> ReadInt64( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], long[]>() {
                @Override
                public long[] Action(byte[] content) {
                    return getByteTransform().TransInt64(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransInt64(content, startIndexSecond < 0 ? 0 : startIndexSecond * 8, length);
            }
        });
    }


    public OperateResultExOne<double[]> ReadDouble( String address, short length ) {
        if (length == 1)
            return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], double[]>() {
                @Override
                public double[] Action(byte[] content) {
                    return getByteTransform().TransDouble(content, 0, length);
                }
            });

        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = extra.Content1;
            address = extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], double[]>() {
            @Override
            public double[] Action(byte[] content) {
                return getByteTransform().TransDouble(content, startIndexSecond < 0 ? 0 : startIndexSecond * 8, length);
            }
        });
    }

    @Override
    public OperateResultExOne<String> ReadString( String address, short length, Charset encoding ) {
        OperateResultExOne<byte[]> read = Read(address, length);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        int strLen = getByteTransform().TransUInt16(read.Content, 0);
        try {
            return OperateResultExOne.CreateSuccessResult(new String(read.Content, 2, strLen, encoding));
        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Word, getByteTransform().TransByte( values ), 1 );
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_DWord, getByteTransform().TransByte( values ), 1 );
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Real, getByteTransform().TransByte( values ), 1 );
    }

    @Override
    public OperateResult Write(String address, long[] values) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_LInt, getByteTransform().TransByte( values ), 1 );
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Double, getByteTransform().TransByte( values ), 1 );
    }

    public OperateResult Write(String address, String value, Charset encoding ) {
        if (Utilities.IsStringNullOrEmpty(value)) value = "";

        byte[] content = SoftBasic.ArrayExpandToLengthEven(value.getBytes(encoding));
        byte[] data = new byte[2 + content.length];
        System.arraycopy(content, 0, data, 2, content.length);
        data[0] = Utilities.getBytes(data.length - 2)[0];
        data[1] = Utilities.getBytes(data.length - 2)[1];
        return super.WriteTag(address, (short) AllenBradleyHelper.CIP_Type_String, data, (short) 1);
    }

    public OperateResult Write( String address, byte value ) {
        return WriteTag(address, (short) 0xD1, new byte[]{value});
    }



    public String toString( ) {
		    return "OmronCipNet[" + getIpAddress() + ":" + getPort() + "]";
    }

}
