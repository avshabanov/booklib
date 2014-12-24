<#import "template/page.ftl" as pt/>
<@pt.page title="Authors">

<#--
  Provides a list of author name prefixes
-->

<h2>Authors</h2>

<p>
  <#list prefixList as namePrefix>
    <span class="named-value-elem"><a href="/g/authors?namePrefix=${namePrefix?url}"><strong>${namePrefix}</strong>&nbsp;<small>&hellip;</small></a></span>
  </#list>
</p>

</@pt.page>
