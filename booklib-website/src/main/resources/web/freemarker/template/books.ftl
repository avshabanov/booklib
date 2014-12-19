<#macro book title authors genres>
<li>
  <span>${title?html}</span>
  <span>&nbsp;|&nbsp;</span>
  <#list authors as author>
    <a href="#" about="Author# ${author.id}">${author.name}</a><#if author_has_next>,&nbsp</#if>
  </#list>
  <span>&nbsp;|&nbsp;</span>
  <#list genres as genre>
    <a href="#" about="Genre# ${genre.id}">${genre.name}</a><#if genre_has_next>,&nbsp</#if>
  </#list>
</li>
</#macro>