package com.iteaj.plugin.protocol.plc.core.transfer;

import java.nio.charset.Charset;

public interface IByteTransform {

    /**
     * 从缓存中提取出bool结果，需要传入想要提取的位索引，注意：是从0开始的位索引，10则表示 buffer[1] 的第二位。<br />
     * To extract the bool result from the cache, you need to pass in the bit index you want to extract. Note: the bit index starts from 0, and 10 represents the second bit of buffer[1].
     * @param buffer 缓存数据
     * @param index 位的索引，注意：是从0开始的位索引，10则表示 buffer[1] 的第二位。
     * @return boolean值
     */
    boolean TransBool( byte[] buffer, int index );

    /**
     * 从缓存中提取出bool数组结果，需要传入想要提取的位索引，注意：是从0开始的位索引，10则表示 buffer[1] 的第二位。长度为 bool 数量的长度，传入 10 则获取 10 个长度的 bool[] 数组。<br />
     * To extract the result of the bool array from the cache, you need to pass in the bit index you want to extract. Note: the bit index starts from 0,
     * and 10 represents the second bit of buffer[1]. The length is the length of the number of bools. If you pass in 10, you will get a bool[] array of 10 lengths.
     * @param buffer 缓存数据
     * @param index 位的索引
     * @param length bool长度
     * @return bool数组
     */
    boolean[] TransBool( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取byte结果，需要指定起始的字节索引<br />
     * To extract the byte result from the cache, you need to specify the starting byte index
     * @param buffer 缓存数据
     * @param index 索引位置
     * @return byte对象
     */
    byte TransByte( byte[] buffer, int index );

    /**
     * 从缓存中提取byte数组结果，需要指定起始的字节索引，以及指定读取的字节长度<br />
     * To extract the byte array result from the cache, you need to specify the starting byte index and the byte length to be read
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return byte数组
     */
    byte[] TransByte( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取short结果，需要指定起始的字节索引，按照字节为单位，一个short占用两个字节<br />
     * To extract short results from the cache, you need to specify the starting byte index, in bytes, A short occupies two bytes
     * @param buffer 缓存数据
     * @param index 索引位置
     * @return short对象
     */
    short TransInt16( byte[] buffer, int index );

    /**
     * 从缓存中提取short数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 short 数组的长度，如果传入 10 ，则表示提取 10 个连续的 short 数据，该数据共占用 20 字节。<br />
     * To extract the result of the short array from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the extracted short array. If 10 is passed in, it means to extract 10 consecutive short data. Occupies 20 bytes.
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return short数组对象
     */
    short[] TransInt16( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取ushort结果，需要指定起始的字节索引，按照字节为单位，一个ushort占用两个字节<br />
     * To extract ushort results from the cache, you need to specify the starting byte index, in bytes, A ushort occupies two bytes
     * @param buffer 缓存数据
     * @param index 索引位置
     * @return short对象
     */
    int TransUInt16( byte[] buffer, int index );

    /**
     * 从缓存中提取ushort数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 ushort 数组的长度，如果传入 10 ，则表示提取 10 个连续的 ushort 数据，该数据共占用 20 字节。<br />
     * To extract the ushort array result from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the extracted ushort array. If 10 is passed in, it means to extract 10 consecutive ushort data. Occupies 20 bytes.
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return short数组对象
     */
    int[] TransUInt16( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取int结果，需要指定起始的字节索引，按照字节为单位，一个int占用四个字节<br />
     * To extract the int result from the cache, you need to specify the starting byte index, in bytes, A int occupies four bytes
     * @param buffer 缓存数据
     * @param index 索引位置
     * @return int对象
     */
    int TransInt32( byte[] buffer, int index );

    /**
     * 从缓存中提取int数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 int 数组的长度，如果传入 10 ，则表示提取 10 个连续的 int 数据，该数据共占用 40 字节。<br />
     * To extract the int array result from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the extracted int array. If 10 is passed in, it means to extract 10 consecutive int data. Occupies 40 bytes.
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return int数组对象
     */
    int[] TransInt32( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取uint结果，需要指定起始的字节索引，按照字节为单位，一个uint占用四个字节<br />
     * To extract uint results from the cache, you need to specify the starting byte index, in bytes, A uint occupies four bytes
     * @param buffer 缓存数据
     * @param index 索引位置
     * @return int对象
     */
    long TransUInt32( byte[] buffer, int index );

    /**
     * 从缓存中提取uint数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 uint 数组的长度，如果传入 10 ，则表示提取 10 个连续的 uint 数据，该数据共占用 40 字节。<br />
     * To extract the uint array result from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the extracted uint array. If 10 is passed in, it means to extract 10 consecutive uint data. Occupies 40 bytes.
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return int数组对象
     */
    long[] TransUInt32( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取long结果，需要指定起始的字节索引，按照字节为单位，一个long占用八个字节<br />
     * To extract the long result from the cache, you need to specify the starting byte index, in bytes, A long occupies eight bytes
     * @param buffer 缓存数据
     * @param index 索引位置
     * @return long对象
     */
    long TransInt64( byte[] buffer, int index );

    /**
     * 从缓存中提取long数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 long 数组的长度，如果传入 10 ，则表示提取 10 个连续的 long 数据，该数据共占用 80 字节。<br />
     * To extract the long array result from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the long array to be extracted. If 10 is passed in, it means to extract 10 consecutive long data. Occupies 80 bytes.
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return long数组对象
     */
    long[] TransInt64( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取float结果，需要指定起始的字节索引，按照字节为单位，一个float占用四个字节<b />
     * To extract the float result from the cache, you need to specify the starting byte index, in units of bytes, A float occupies four bytes
     * @param buffer 缓存对象
     * @param index 索引位置
     * @return float对象
     */
    float TransSingle( byte[] buffer, int index );

    /**
     * 从缓存中提取float数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 float 数组的长度，如果传入 10 ，则表示提取 10 个连续的 float 数据，该数据共占用 40 字节。<br />
     * To extract the result of the float array from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the extracted float array. If 10 is passed in, it means that 10 consecutive float data are extracted. Occupies 40 bytes.
     * @param buffer 缓存数据
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return float数组对象
     */
    float[] TransSingle( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取double结果，需要指定起始的字节索引，按照字节为单位，一个double占用八个字节<br />
     * To extract the double result from the cache, you need to specify the starting byte index, in bytes, A double occupies eight bytes
     * @param buffer 缓存对象
     * @param index 索引位置
     * @return double对象
     */
    double TransDouble( byte[] buffer, int index );

    /**
     * 从缓存中提取double数组结果，需要指定起始的字节索引，按照字节为单位，然后指定提取的 double 数组的长度，如果传入 10 ，则表示提取 10 个连续的 double 数据，该数据共占用 80 字节。<br />
     * To extract the double array result from the cache, you need to specify the starting byte index, in bytes,
     * and then specify the length of the extracted double array. If 10 is passed in, it means to extract 10 consecutive double data. Occupies 80 bytes.
     * @param buffer 缓存对象
     * @param index 索引位置
     * @param length 读取的数组长度
     * @return double数组
     */
    double[] TransDouble( byte[] buffer, int index, int length );

    /**
     * 从缓存中提取string结果，使用指定的编码将全部的缓存转为字符串<br />
     * Extract the string result from the cache, use the specified encoding to convert all the cache into a string
     * @param buffer 缓存对象
     * @param index 索引位置
     * @param length byte数组长度
     * @param encoding 字符串的编码
     * @return string对象
     */
    String TransString( byte[] buffer, int index, int length, Charset encoding );






    /**
     * bool变量转化缓存数据，一般来说单bool只能转化为0x01 或是 0x00<br />
     * The bool variable is converted to cache data, a single bool can only be converted to 0x01 or 0x00
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( boolean value );

    /**
     * 将bool数组变量转化缓存数据，如果数组长度不满足8的倍数，则自动补0操作。<br />
     * Convert the bool array variable to the cache data. If the length of the array does not meet a multiple of 8, it will automatically add 0.
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte( boolean[] values );

    /**
     * 将byte变量转化缓存数据<br />
     * Convert byte variables into cached data
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( byte value );

    /**
     * short变量转化缓存数据，一个short数据可以转为2个字节的byte数组<br />
     * Short variable is converted to cache data, a short data can be converted into a 2-byte byte array
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( short value );

    /**
     * short数组变量转化缓存数据，n个长度的short数组，可以转为2*n个长度的byte数组<br />
     * The short array variable transforms the buffered data, a short array of n lengths can be converted into a byte array of 2*n lengths
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte( short[] values );

    /**
     * int变量转化缓存数据，一个int数据可以转为4个字节的byte数组<br />
     * Int variable converts cache data, an int data can be converted into a 4-byte byte array
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( int value );

    /**
     * int数组变量转化缓存数据，n个长度的int数组，可以转为4*n个长度的byte数组<br />
     * The int array variable transforms the cache data, the int array of n length can be converted to the byte array of 4*n length
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte( int[] values );

    /**
     * long变量转化缓存数据，一个long数据可以转为8个字节的byte数组<br />
     * Long variable is converted into cache data, a long data can be converted into 8-byte byte array
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( long value );

    /**
     * long数组变量转化缓存数据，n个长度的long数组，可以转为8*n个长度的byte数组<br />
     * The long array variable transforms the buffer data, the long array of n length can be converted into the byte array of 8*n length
     * @param values 等待转化的数组
     * @return v
     */
    byte[] TransByte( long[] values );

    /**
     * float变量转化缓存数据，一个float数据可以转为4个字节的byte数组<br />
     * Float variable is converted into cache data, a float data can be converted into a 4-byte byte array
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( float value );

    /**
     * float数组变量转化缓存数据，n个长度的float数组，可以转为4*n个长度的byte数组<br />
     * Float array variable converts buffer data, n-length float array can be converted into 4*n-length byte array
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte( float[] values );

    /**
     * double变量转化缓存数据，一个double数据可以转为8个字节的byte数组<br />
     * The double variable is converted to cache data, a double data can be converted into an 8-byte byte array
     * @param value 等待转化的数据
     * @return buffer数据
     */
    byte[] TransByte( double value );

    /**
     * double数组变量转化缓存数据，n个长度的double数组，可以转为8*n个长度的byte数组<br />
     * The double array variable transforms the buffer data, the double array of n length can be converted to the byte array of 8*n length
     * @param values 等待转化的数组
     * @return buffer数据
     */
    byte[] TransByte( double[] values );

    /**
     * 使用指定的编码字符串转化缓存数据<br />
     * Use the specified encoding string to convert the cached data
     * @param value 等待转化的数据
     * @param encoding 字符串的编码方式
     * @return buffer数据
     */
    byte[] TransByte( String value, Charset encoding );

    /**
     * 设置数据解析的格式，可选ABCD, BADC，CDAB，DCBA格式，对int,uint,float,double,long,ulong类型有作用
     * Set the format of the data analysis, optional ABCD, BADC, CDAB, DCBA format, effective for int, uint, float, double, long, ulong type
     * @param dataFormat 数据格式
     */
    void setDataFormat( DataFormat dataFormat );

    /**
     * 获取数据解析的格式，可选ABCD, BADC，CDAB，DCBA格式，对int,uint,float,double,long,ulong类型有作用
     * Get the format of the data analysis, optional ABCD, BADC, CDAB, DCBA format, effective for int, uint, float, double, long, ulong type
     * @return 数据格式
     */
    DataFormat getDataFormat();

    /**
     * 获取在解析字符串的时候是否将字节按照字单位反转<br />
     * Gets whether to reverse the bytes in word units when parsing strings
     * @return 是否反转
     */
    boolean getIsStringReverse();

    /**
     * 设置在解析字符串的时候是否将字节按照字单位反转<br />
     * Sets whether to reverse the bytes in word units when parsing strings
     * @param value 设置值
     */
    void setIsStringReverse(boolean value);

    /**
     * 根据指定的 {@link DataFormat} 格式，来实例化一个新的对象，除了{@link DataFormat}不同，其他都相同<br />
     * ccording to the specified {@link DataFormat} format, to instantiate a new object, except that {@link DataFormat} is different, everything else is the same
     * @param dataFormat 数据格式
     * @return 对象
     */
    IByteTransform CreateByDateFormat( DataFormat dataFormat );

}
