<#import "template/page.ftl" as pt/>
<@pt.page title="Authors">

<#--
  Provides a list of author name prefixes
-->

<h2>Authors</h2>

<ul>
  <#list prefixList as namePrefix>
    <li><a href="/g/authors?namePrefix=${namePrefix?html}">${namePrefix}</a></li>
  </#list>
</ul>

</@pt.page>
