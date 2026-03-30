# 移动端接口列表
|名称|url|方法|说明|
|---|---|---|---|
<#if entityList?has_content>
<#list entityList as entity>
|${entity.name!''}|${entity.url!''}|${entity.method!''}|${entity.remark!''}|
</#list>
</#if>
