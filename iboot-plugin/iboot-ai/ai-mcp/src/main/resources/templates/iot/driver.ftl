# 设备驱动列表
|名称|代码|版本|传输协议|网关类型|说明|
|---|---|---|---|---|---|---|---|
<#if drivers?has_content>
<#list drivers as driver>
|${driver.name!''}|${driver.code!''}|${driver.version!''}|${driver.type!''}|${driver.gatewayType!''}|${driver.remark!''}|
</#list>
</#if>
