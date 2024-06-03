## iboot(物联网网关)
iboot是基于java1.8、springboot2.7、netty等框架开发的物联网网关，支持的网络协议包括tcp、udp、mqtt、http、串口等，工业协议包括modbus、plc(欧姆龙、西门子)
- [演示地址(支持DTU、PLC、MODBUS、串口等调试)](https://iot.iteaj.com/#/login)
- [项目前端仓库(vue3+antdv3+vite2)](https://gitee.com/iteaj/ivzone)
#### 项目介绍
1. 这个项目能用来做什么<br>
    它是一款通用的物联网网关，用来连接业务系统和物联网设备的平台；主要用于采集和控制设备，对物联网不熟的个人或者公司可以更加关注业务需求，设备相关部分交由物联网网关
2. 既然设备的采集和控制交给iboot，那业务系统怎么和设备交互呢<br>
    iboot支持通过http协议以同步的方式以及通过mqtt异步的方式和业务系统交互，业务系统可以间接通过iboot网关采集和控制设备

#### 语言和框架
1. 后端：java1.8、spring boot2、、mybatis、mybatis-plus、shiro、thymeleaf、hikaricp
2. 前端：vue2.6、antd、axios、qs、moment、validate
3. 数据： mysql5.7+
4. 开发工具：idea

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
