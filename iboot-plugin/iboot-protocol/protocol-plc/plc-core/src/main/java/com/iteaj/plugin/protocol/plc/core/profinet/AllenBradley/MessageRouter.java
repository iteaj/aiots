package com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;

import java.nio.charset.StandardCharsets;

public class MessageRouter {

    /**
     * 实例化一个默认的实例对象<br />
     * instantiate a default instance object
     */
    public MessageRouter( )
    {
        _router[0] = 0x01;
        System.arraycopy(new byte[] { 0x0f, 0x02, 0x12, 0x01 },0,_router, 1, 4);
        _router[5] = 0x0c;
    }

    /**
     * 指定路由来实例化一个对象，使用字符串的表示方式<br />
     * Specify the route to instantiate an object, using the string representation<br />
     * 路有消息支持两种格式，格式1：1.15.2.18.1.12   格式2： 1.1.2.130.133.139.61.1.0<br />
     * There are two formats for the channel message, format 1: 1.15.2.18.1.12 format 2: 1.1.2.130.133.139.61.1.0
     * @param router 路由信息
     */
    public MessageRouter( String router )
    {
        String[] splits = router.split( "\\.");
        if (splits.length <= 6)
        {
            if (splits.length > 0) _router[0] = (byte) Integer.parseInt( splits[0] );
            if (splits.length > 1) _router[1] = (byte) Integer.parseInt( splits[1] );
            if (splits.length > 2) _router[2] = (byte) Integer.parseInt( splits[2] );
            if (splits.length > 3) _router[3] = (byte) Integer.parseInt( splits[3] );
            if (splits.length > 4) _router[4] = (byte) Integer.parseInt( splits[4] );
            if (splits.length > 5) _router[5] = (byte) Integer.parseInt( splits[5] );
        }
        else if (splits.length == 9)
        {
            String ip = splits[3] + "." + splits[4] + "." + splits[5] + "." + splits[6];
            _router = new byte[6 + ip.length()];
            _router[0] = (byte) Integer.parseInt( splits[0] );
            _router[1] = (byte) Integer.parseInt( splits[1] );
            _router[2] = (byte)(0x10 + Integer.parseInt( splits[2] ));
            _router[3] = (byte)ip.length();
            byte[] bufferIp = ip.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(bufferIp, 0, _router, 4, bufferIp.length);
            _router[_router.length - 2] = (byte) Integer.parseInt( splits[7] );
            _router[_router.length - 1] = (byte) Integer.parseInt( splits[8] );
        }
    }

    /**
     * 使用完全自定义的消息路由来初始化数据<br />
     * Use fully custom message routing to initialize data
     * @param router 完全自定义的路由消息
     */
    public MessageRouter( byte[] router ) {
        _router = router;
    }

    /**
     * 获取路由信息
     * @return 路由消息的字节信息
     */
    public byte[] GetRouter( ) {
        return _router;
    }

    /**
     * 获取用于发送的CIP路由报文信息<br />
     *  Get information about CIP routing packets for sending
     * @return 路由信息
     */
    public byte[] GetRouterCIP( ) {
        byte[] router = this.GetRouter();
        if (router.length % 2 == 1) router = SoftBasic.SpliceArray(router, new byte[]{0x00});  // 奇数长度的情况补0操作
        byte[] routerCip = new byte[46 + router.length];

        byte[] buffer1 = SoftBasic.HexStringToBytes("54022006240105f70200 00800100fe8002001b05 28a7fd03020000008084 1e00f44380841e00f443 a305");
        System.arraycopy(buffer1, 0, routerCip, 0, buffer1.length);
        System.arraycopy(router, 0, routerCip, 42, router.length);
        System.arraycopy(new byte[]{0x20, 0x02, 0x24, 0x01}, 0, routerCip, 42 + router.length, 4);
        routerCip[41] = (byte) (router.length / 2);
        return routerCip;
    }

    /**
     * 获取背板信息
     */
    public  byte getBackplane(){
        return _router[0];
    }

    /**
     * 设置背板信息
     * @param value 值
     */
    public void setBackplane(byte value){
        _router[0] = value;
    }

    /**
     * 获取槽号信息
     * @return 值
     */
    public byte getSlot(){
        return _router[5];
    }

    public void setSlot(byte value){
        _router[5] = value;
    }


    private byte[] _router = new byte[6];
}
