## iboot(物联网网关)
iboot是基于java1.8、springboot2.7、netty等框架开发的物联网网关，支持的网络协议包括tcp、udp、mqtt、http、串口等，工业协议包括modbus、plc(欧姆龙、西门子)
- [演示地址(支持DTU、PLC、MODBUS、串口等调试)](https://iot.iteaj.com/#/login)
- [项目前端仓库(vue3+antdv3+vite2)](https://gitee.com/iteaj/ivzone)
### 项目介绍
1. ##### 这个项目能用来做什么<br>
      iboot是一款通用的物联网关平台，用来连接业务系统和物联网设备的网关；对南向主要用于采集和控制设备对北向主要是提供接口给第三方业务平台操作设备，使得对物联网不熟的个人开发者或者公司可以更加关注业务需求，将设备相关部分交由iboot物联网关
2. ##### 既然设备的采集和控制交给iboot，那业务系统怎么和设备交互呢<br>
      iboot支持通过http协议以同步的方式以及通过mqtt异步的方式和业务系统交互，业务系统可以间接通过iboot网关采集和控制设备
3. ##### 如果你们项目不大，设备量也不多不想要太复杂的软件架构，要的是轻装上阵开发效率第一怎么办<br>
      iboot只是一个springboot单体项目，只依赖mysql服务和nginx服务就可以，部署简单，运行效率高
4. ##### 我们设备种类很多，iboot支持自定义设备接入吗<br>
      iboot支持以自定义jar包的方式提供设备驱动, 理论上可以支持任何设备
5. ##### 我么设备量也多，采集的数据量就一台mysql肯定扛不住要怎么办<br>

      
      

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
