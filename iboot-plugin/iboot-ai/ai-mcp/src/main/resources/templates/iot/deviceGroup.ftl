# 设备组列表
| 名称 | id | 地址 | 说明 |
|---|---|---|---|---|---|---|---|
<#if groups?has_content>
<#list groups as group>
|${group.name!''}|${group.id!''}|${group.address!''}|${group.remark!''}|
</#list>
</#if>
