# 产品信息

## 基本信息
- 产品码: ${product.code!''}
- 产品名称: ${product.name!''}
- 产品状态: ${product.status!''}
- 产品说明: ${product.remark!''}

## 物模型指令列表

| 指令码 | 指令名称 | 指令说明 |
|--------|----------|------|
<#if product.directs?has_content>
<#list product.directs as direct>
| ${direct.apiName!''} | ${direct.CommandValue!''} | ${direct.remark!''} |
</#list>
</#if>

## 产品设备列表
<#if devices?has_content>
| 设备UID | 设备名称 | 在线状态 | 所属组 |
|---------|----------|------|--------|
<#list devices as device>
| ${device.uid!''} | ${device.deviceName!''} | ${device.deviceStatus!''} | ${device.deviceGroupId!''} |
</#list>
</#if>
