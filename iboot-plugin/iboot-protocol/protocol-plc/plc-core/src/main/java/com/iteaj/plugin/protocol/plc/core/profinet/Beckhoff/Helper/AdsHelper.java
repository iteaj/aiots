package com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff.Helper;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff.AmsTcpHeaderFlags;
import com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff.BeckhoffCommandId;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.ArrayList;

public class AdsHelper {

    // region Build Command

    /**
     * 根据命令码ID，消息ID，数据信息组成AMS的命令码
     * @param commandId 命令码ID
     * @param data 数据内容
     * @return 打包之后的数据信息，没有填写AMSNetId的Target和Source内容
     */
    public static byte[] BuildAmsHeaderCommand( int commandId, byte[] data ) {
        // 还有三个参数，在通信的时候，动态赋值，source netid，target netid，以及 invoke id
        if (data == null) data = new byte[0];

        byte[] buffer = new byte[32 + data.length];
        buffer[16] = BitConverter.GetBytes(commandId)[0];           // Command ID
        buffer[17] = BitConverter.GetBytes(commandId)[1];
        buffer[18] = 0x04;                                            // flag: Tcp, Request
        buffer[19] = 0x00;
        buffer[20] = BitConverter.GetBytes(data.length)[0];         // Size of the data range. The unit is byte
        buffer[21] = BitConverter.GetBytes(data.length)[1];
        buffer[22] = BitConverter.GetBytes(data.length)[2];
        buffer[23] = BitConverter.GetBytes(data.length)[3];
        buffer[24] = 0x00;                                            // AMS error number
        buffer[25] = 0x00;
        buffer[26] = 0x00;
        buffer[27] = 0x00;
        Utilities.ByteArrayCopyTo(data, buffer, 32);

        return PackAmsTcpHelper(AmsTcpHeaderFlags.Command, buffer);
    }

    /**
     * 构建读取设备信息的命令报文
     * @return 报文信息
     */
    public static OperateResultExOne<byte[]> BuildReadDeviceInfoCommand( ) {
        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.ReadDeviceInfo, null));
    }

    /**
     * 构建读取状态的命令报文
     * @return 报文信息
     */
    public static OperateResultExOne<byte[]> BuildReadStateCommand( ) {
        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.ReadState, null));
    }

    /**
     * 构建写入状态的命令报文
     * @param state Ads state
     * @param deviceState Device state
     * @param data Data
     * @return 报文信息
     */
    public static OperateResultExOne<byte[]> BuildWriteControlCommand( short state, short deviceState, byte[] data ) {
        if (data == null) data = new byte[0];
        byte[] buffer = new byte[8 + data.length];

        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.WriteControl,
                SoftBasic.SpliceArray(BitConverter.GetBytes(state), BitConverter.GetBytes(deviceState), BitConverter.GetBytes(data.length), data)));
    }

    /**
     * 构建写入的指令信息
     * @param address 地址信息
     * @param length 数据长度
     * @param isBit 是否是位信息
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( String address, int length, boolean isBit ) {
        OperateResultExTwo<Integer, Integer> analysis = AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] data = new byte[12];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), data, 8);
        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.Read, data));
    }

    /**
     * 构建批量读取的指令信息，不能传入读取符号数据，只能传入读取M,I,Q,i=0x0001信息
     * @param address 地址信息
     * @param length 数据长度
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( String[] address, short[] length ) {
        byte[] data = new byte[12 * address.length];
        int lenCount = 0;
        for (int i = 0; i < address.length; i++) {
            OperateResultExTwo<Integer, Integer> analysis = AnalysisAddress(address[i], false);
            if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content1), data, 12 * i + 0);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content2), data, 12 * i + 4);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((int) length[i]), data, 12 * i + 8);
            lenCount += length[i];
        }

        return BuildReadWriteCommand("ig=0xF080;0", lenCount, false, data);
    }

    /**
     * 构建写入的指令信息
     * @param address 地址信息
     * @param length 数据长度
     * @param isBit 是否是位信息
     * @param value 写入的数值
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildReadWriteCommand( String address, int length, boolean isBit, byte[] value ) {
        OperateResultExTwo<Integer, Integer> analysis = AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] data = new byte[16 + value.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), data, 8);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length), data, 12);
        Utilities.ByteArrayCopyTo(value, data, 16);

        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.ReadWrite, data));
    }

    /**
     * 构建批量写入的指令代码，不能传入读取符号数据，只能传入读取M,I,Q,i=0x0001信息
     *
     * 实际没有调试通
     * @param address 地址列表信息
     * @param value 写入的数据值信息
     * @return 命令报文
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( String[] address, ArrayList<byte[]> value ) {
        MemoryStream ms = new MemoryStream();
        int lenCount = 0;
        for (int i = 0; i < address.length; i++) {
            OperateResultExTwo<Integer, Integer> analysis = AnalysisAddress(address[i], false);
            if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

            ms.Write(BitConverter.GetBytes(analysis.Content1));
            ms.Write(BitConverter.GetBytes(analysis.Content2));
            ms.Write(BitConverter.GetBytes(value.get(i).length));
            ms.Write(value.get(i));
            lenCount += value.get(i).length;
        }
        //for (int i = 0; i < value.Count; i++)
        //{
        //	ms.Write( value[i] );
        //}

        return BuildReadWriteCommand("ig=0xF081;0", lenCount, false, ms.ToArray());
    }

    /**
     * 构建写入的指令信息
     * @param address 地址信息
     * @param value 数据
     * @param isBit 是否是位信息
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( String address, byte[] value, boolean isBit ) {
        OperateResultExTwo<Integer, Integer> analysis = AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] data = new byte[12 + value.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length), data, 8);
        Utilities.ByteArrayCopyTo(value, data, 12);

        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.Write, data));
    }

    /**
     * 构建写入的指令信息
     * @param address 地址信息
     * @param value 数据
     * @param isBit 是否是位信息
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( String address, boolean[] value, boolean isBit ) {
        OperateResultExTwo<Integer, Integer> analysis = AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] buffer = Array.GetByteFromBoolArray(value);
        byte[] data = new byte[12 + buffer.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(buffer.length), data, 8);
        Utilities.ByteArrayCopyTo(buffer, data, 12);

        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.Write, data));
    }

    /**
     * 构建释放句柄的报文信息，当获取了变量的句柄后，这个句柄就被释放
     * @param handle 句柄信息
     * @return 报文的结果内容
     */
    public static OperateResultExOne<byte[]> BuildReleaseSystemHandle( int handle ) {
        byte[] data = new byte[16];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(0xF006), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(0x0004), data, 8);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(handle), data, 12);

        return OperateResultExOne.CreateSuccessResult(BuildAmsHeaderCommand(BeckhoffCommandId.Write, data));
    }

    // endregion


    // region Static Method

    /**
     * 检查从PLC的反馈的数据报文是否正确
     * @param response 反馈报文
     * @return 检查结果
     */
    public static OperateResultExOne<Integer> CheckResponse( byte[] response ) {
        try {
            int ams = BitConverter.ToInt32(response, 30);
            if (ams > 0)
                return new OperateResultExOne<Integer>(ams, GetErrorCodeText(ams) + "\r\nSource:" + SoftBasic.ByteToHexString(response, ' '));

            if (response.length >= 42) {
                int status = BitConverter.ToInt32(response, 38);
                if (status != 0)
                    return new OperateResultExOne(status, GetErrorCodeText(status) + "\r\nSource:" + SoftBasic.ByteToHexString(response, ' '));
            }
        } catch (Exception ex) {
            return new OperateResultExOne<Integer>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(response, ' '));
        }

        return OperateResultExOne.CreateSuccessResult(0);
    }


    /**
     * 将实际的包含AMS头报文和数据报文的命令，打包成实际可发送的命令
     * @param headerFlags 命令头信息
     * @param command 命令信息
     * @return 结果信息
     */
    public static byte[] PackAmsTcpHelper( int headerFlags, byte[] command ) {
        byte[] buffer = new byte[6 + command.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short) headerFlags), buffer, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(command.length), buffer, 2);
        Utilities.ByteArrayCopyTo(command, buffer, 6);
        return buffer;
    }

    private static int CalculateAddressStarted( String address ) {
        if (address.indexOf('.') < 0) {
            return Convert.ToInt32(address);
        } else {
            String[] temp = Utilities.SplitDot(address);
            return Convert.ToInt32(temp[0]) * 8 + PlcHelper.CalculateBitStartIndex(temp[1]);
        }
    }

    /**
     * 分析当前的地址信息，根据结果信息进行解析出真实的偏移地址
     * @param address 地址
     * @param isBit 是否位访问
     * @return 结果内容
     */
    public static OperateResultExTwo<Integer, Integer> AnalysisAddress( String address, boolean isBit ) {
        OperateResultExTwo<Integer, Integer> result = new OperateResultExTwo<Integer, Integer>();
        try {
            if (address.startsWith("i=") || address.startsWith("I=")) {
                result.Content1 = 0xF005;
                result.Content2 = (int) Long.parseLong(address.substring(2));
            } else if (address.startsWith("s=") || address.startsWith("S=")) {
                result.Content1 = 0xF003;
                result.Content2 = 0x0000;
            } else if (address.startsWith("ig=") || address.startsWith("IG=")) {
                OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "ig", 0);
                if (extra.IsSuccess) {
                    address = extra.Content2;
                    result.Content1 = extra.Content1;
                }

                result.Content2 = (int) Long.parseLong(address);
            } else {
                switch (address.charAt(0)) {
                    case 'M':
                    case 'm': {
                        if (isBit) {
                            result.Content1 = 0x4021;
                            result.Content2 = CalculateAddressStarted(address.substring(1));
                        } else {
                            result.Content1 = 0x4020;
                            result.Content2 = (int) Long.parseLong(address.substring(1));
                        }
                        break;
                    }
                    case 'I':
                    case 'i': {
                        if (isBit) {
                            result.Content1 = 0xF021;
                            result.Content2 = CalculateAddressStarted(address.substring(1)) + 0x000FA000;
                        } else {
                            result.Content1 = 0xF020;
                            result.Content2 = (int) Long.parseLong(address.substring(1)) + 0x0001F400;
                        }
                        break;
                    }
                    case 'Q':
                    case 'q': {
                        if (isBit) {
                            result.Content1 = 0xF031;
                            result.Content2 = CalculateAddressStarted(address.substring(1)) + 0x001F4000;
                        } else {
                            result.Content1 = 0xF030;
                            result.Content2 = (int) (Long.parseLong(address.substring(1)) + 0x0003E800);
                        }
                        break;
                    }
                    default:
                        throw new Exception(StringResources.Language.NotSupportedDataType());

                }
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        result.Message = StringResources.Language.SuccessText();
        return result;
    }

    /**
     * 将字符串名称转变为ADS协议可识别的字节数组
     * @param value 值
     * @return 字节数组
     */
    public static byte[] StrToAdsBytes( String value ) {
        return SoftBasic.SpliceArray(Encoding.ASCII.GetBytes(value), new byte[1]);
    }

    /**
     * 将字符串的信息转换为AMS目标的地址
     * @param amsNetId 目标信息
     * @return 字节数组
     */
    public static byte[] StrToAMSNetId( String amsNetId ) {
        byte[] buffer;
        String ip = amsNetId;
        if (amsNetId.indexOf(':') > 0) {
            buffer = new byte[8];
            String[] ipPort = amsNetId.split(":");
            ip = ipPort[0];
            buffer[6] = BitConverter.GetBytes(Integer.parseInt(ipPort[1]))[0];
            buffer[7] = BitConverter.GetBytes(Integer.parseInt(ipPort[1]))[1];
        } else {
            buffer = new byte[6];
        }
        String[] ips = Utilities.SplitDot(ip);
        for (int i = 0; i < ips.length; i++) {
            buffer[i] = (byte) Integer.parseInt(ips[i]);
        }
        return buffer;
    }

    /**
     * 根据byte数组信息提取出字符串格式的AMSNetId数据信息，方便日志查看
     * @param data 原始的报文数据信息
     * @param index 起始的节点信息
     * @return Ams节点号信息
     */
    public static String GetAmsNetIdString( byte[] data, int index ) {
        StringBuilder sb = new StringBuilder();
        sb.append(data[index]&0xff);
        sb.append(".");
        sb.append(data[index + 1]&0xff);
        sb.append(".");
        sb.append(data[index + 2]&0xff);
        sb.append(".");
        sb.append(data[index + 3]&0xff);
        sb.append(".");
        sb.append(data[index + 4]&0xff);
        sb.append(".");
        sb.append(data[index + 5]&0xff);
        sb.append(":");
        sb.append(BitConverter.ToUInt16(data, index + 6));
        return sb.toString();
    }

    /**
     * 根据AMS的错误号，获取到错误信息，错误信息来源于 wirshake 源代码文件 "..\wireshark\plugins\epan\ethercat\packet-ams.c"
     * @param error 错误号
     * @return 错误的描述信息
     */
    public static String GetErrorCodeText( int error )
    {
        switch (error)
        {
            case  0: return "NO ERROR";
            case  1: return "InternalError";
            case  2: return "NO RTIME";
            case  3: return "Allocation locked – memory error.";
            case  4: return "Mailbox full – the ADS message could not be sent. Reducing the number of ADS messages per cycle will help.";
            case  5: return "WRONG RECEIVEH MSG";
            case  6: return "Target port not found – ADS server is not started or is not reachable.";
            case  7: return "Target computer not found – AMS route was not found.";
            case  8: return "Unknown command ID.";
            case  9: return "Invalid task ID.";
            case 10: return "No IO.";
            case 11: return "Unknown AMS command.";
            case 12: return "Win32 error.";
            case 13: return "Port not connected.";
            case 14: return "Invalid AMS length.";
            case 15: return "Invalid AMS Net ID.";
            case 16: return "Installation level is too low –TwinCAT 2 license error.";
            case 17: return "No debugging available.";
            case 18: return "Port disabled – TwinCAT system service not started.";
            case 19: return "Port already connected.";
            case 20: return "AMS Sync Win32 error.";
            case 21: return "AMS Sync Timeout.";
            case 22: return "AMS Sync error.";
            case 23: return "No index map for AMS Sync available.";
            case 24: return "Invalid AMS port.";
            case 25: return "No memory.";
            case 26: return "TCP send error.";
            case 27: return "Host unreachable.";
            case 28: return "Invalid AMS fragment.";
            case 29: return "TLS send error – secure ADS connection failed.";
            case 30: return "Access denied – secure ADS access denied.";
            case 1280: return "Locked memory cannot be allocated.";
            case 1281: return "The router memory size could not be changed.";
            case 1282: return "The mailbox has reached the maximum number of possible messages.";
            case 1283: return "The Debug mailbox has reached the maximum number of possible messages.";
            case 1284: return "The port type is unknown.";
            case 1285: return "The router is not initialized.";
            case 1286: return "The port number is already assigned.";
            case 1287: return "The port is not registered.";
            case 1288: return "The maximum number of ports has been reached.";
            case 1289: return "The port is invalid.";
            case 1290: return "The router is not active.";
            case 1291: return "The mailbox has reached the maximum number for fragmented messages.";
            case 1292: return "A fragment timeout has occurred.";
            case 1293: return "The port is removed.";
            case 1792: return "General device error.";
            case 1793: return "Service is not supported by the server.";
            case 1794: return "Invalid index group.";
            case 1795: return "Invalid index offset.";
            case 1796: return "Reading or writing not permitted.";
            case 1797: return "Parameter size not correct. Commonly found in batch processing, check the calculation command length";
            case 1798: return "Invalid data values.";
            case 1799: return "Device is not ready to operate. It is possible that the TSM configuration is incorrect, reactivate the configuration";
            case 1800: return "Device Busy";
            case 1801: return "Invalid operating system context. This can result from use of ADS blocks in different tasks. It may be possible to resolve this through multitasking synchronization in the PLC.";
            case 1802: return "Insufficient memory.";
            case 1803: return "Invalid parameter values.";
            case 1804: return "Device Not Found";
            case 1805: return "Device Syntax Error";
            case 1806: return "Objects do not match.";
            case 1807: return "Object already exists.";
            case 1808: return "Symbol not found. Check whether the variable name is correct, Note: the global variables in some PLC equipment are: .[Variable Name]";
            case 1809: return "Invalid symbol version. This can occur due to an online change. Create a new handle.";
            case 1810: return "Device (server) is in invalid state.";
            case 1811: return "AdsTransMode not supported.";
            case 1812: return "Device Notify Handle Invalid";
            case 1813: return "Notification client not registered.";
            case 1814: return "Device No More Handles";
            case 1815: return "Device Invalid Watch size";
            case 1816: return "Device Not Initialized";
            case 1817: return "Device TimeOut";
            case 1818: return "Device No Interface";
            case 1819: return "Device Invalid Interface";
            case 1820: return "Device Invalid CLSID";
            case 1821: return "Device Invalid Object ID";
            case 1822: return "Device Request Is Pending";
            case 1823: return "Device Request Is Aborted";
            case 1824: return "Device Signal Warning";
            case 1825: return "Device Invalid Array Index";
            case 1826: return "Device Symbol Not Active";
            case 1827: return "Device Access Denied";
            case 1828: return "Device Missing License";
            case 1829: return "Device License Expired";
            case 1830: return "Device License Exceeded";
            case 1831: return "Device License Invalid";
            case 1832: return "Device License System Id";
            case 1833: return "Device License No Time Limit";
            case 1834: return "Device License Future Issue";
            case 1835: return "Device License Time To Long";
            case 1836: return "Device Exception During Startup";
            case 1837: return "Device License Duplicated";
            case 1838: return "Device Signature Invalid";
            case 1839: return "Device Certificate Invalid";
            case 1840: return "Device License Oem Not Found";
            case 1841: return "Device License Restricted";
            case 1842: return "Device License Demo Denied";
            case 1843: return "Device Invalid Function Id";
            case 1844: return "Device Out Of Range";
            case 1845: return "Device Invalid Alignment";
            case 1846: return "Device License Platform";
            case 1847: return "Device Context Forward Passive Level";
            case 1848: return "Device Context Forward Dispatch Level";
            case 1849: return "Device Context Forward RealTime";
            case 1850: return "Device Certificate Entrust";
            case 1856: return "ClientError";
            case 1857: return "Client Invalid Parameter";
            case 1858: return "Client List Empty";
            case 1859: return "Client Variable In Use";
            case 1860: return "Client Duplicate InvokeID";
            case 1861: return "Timeout has occurred – the remote terminal is not responding in the specified ADS timeout. The route setting of the remote terminal may be configured incorrectly.";
            case 1862: return "ClientW32OR";
            case 1863: return "Client Timeout Invalid";
            case 1864: return "Client Port Not Open";
            case 1865: return "Client No Ams Addr";
            case 1872: return "Client Sync Internal";
            case 1873: return "Client Add Hash";
            case 1874: return "Client Remove Hash";
            case 1875: return "Client No More Symbols";
            case 1876: return "Client Response Invalid";
            case 1877: return "Client Port Locked";
            case 32768: return "ClientQueueFull";
            case 10060: return "A connection timeout has occurred - error while establishing the connection, because the remote terminal did not respond properly after a certain period of time";
            case 10061: return "WSA_ConnRefused";
            case 10065: return "No route to host - a socket operation referred to an unavailable host.";
            default: return StringResources.Language.UnknownError();
        }
    }

    // endregion
}
