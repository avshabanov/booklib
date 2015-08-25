<#macro list valuePageUrlPrefix listModel>
  <p>
    <#list listModel as val>
      <span class="named-value-elem"><a href="${valuePageUrlPrefix}/${val.id?c}"><small>${val.id?c}.</small>&nbsp;<strong>${val.name}</strong></a></span>
    </#list>
  </p>
</#macro>