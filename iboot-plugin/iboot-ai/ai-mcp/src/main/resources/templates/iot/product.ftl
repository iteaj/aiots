# 产品信息

## 基本信息
| 产品名称 | 产品码 | 产品状态 | 设备类型 | 产品类型 | 所属驱动 | 创建时间 | 产品说明 |
|---|---|---|---|---|---|---|---|
<#if products?has_content>
<#list products as product>
| ${product.name!''} | ${product.code!''} | ${product.status!''} | ${product.deviceTypeName!''} | ${product.typeName!''} | ${product.protocolCode!''} | ${product.createTime?string('yy.MM.dd HH:mm:ss')} | ${product.remark!''} |
</#list>
</#if>
