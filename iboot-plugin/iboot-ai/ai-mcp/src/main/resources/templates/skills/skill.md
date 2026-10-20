---
name: aiots
description: 提供 aiots(iboot) 物联网平台的使用手册，包括系统架构、安装部署、设备指令调用、产品信息、物模型等使用和常见问题排查。
---
# 使用场景
- 咨询aiots相关资料时
- 查询aiots指令信息(包括产品对应的指令集和设备uid)
# 系统指令码说明
- 每个产品都有和其对应的指令集和设备列表文件
- 指令码需要严格按照产品指令集文件列出的为准，不能凭空捏造
- 指令码由接口码和可选的接口参数列表组成，接口码和参数及参数与参数之间用&作为分割符。
- 示例: api->ctrl&key1->value1&key2->value2
## 接口码格式
> 示例：api->英文字符组成的接口码
## 接口参数格式
> 示例：key1->value1&key2->value2
## 各产品对应的指令集和设备列表
[各产品对应的指令集和设备列表资料](reference/products_info.md)


# 系统概述

aiots 是一个综合物联网平台，提供以下核心能力：
## 快速导航
根据用户问题类型，选择对应的处理流程：
| 问题类型 | 处理章节 |
|---------|---------|
| 官网地址 | [官网地址](https://www.iteaj.com) |
| 安装/部署 | [安装部署](https://www.iteaj.com/guide/#%E5%AE%89%E8%A3%85%E9%83%A8%E7%BD%B2) |
| 商业版授权 | [授权](https://www.iteaj.com/license/) |
| 试用版下载地址 | [下载](https://www.iteaj.com/download/) |
| 更新日志 | [更新日志](https://www.iteaj.com/change/) |
## 核心组件
1.提供的驱动
|驱动名称|驱动说明|
|------------|-------------|
|Modbus Tcp|标准的modbus tcp协议接入|

