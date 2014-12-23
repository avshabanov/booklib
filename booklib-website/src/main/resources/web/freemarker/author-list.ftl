<#import "template/page.ftl" as pt/>
<@pt.page title="Authors">

<#--
    Provides a list of authors
-->

<h2>Authors</h2>

<ul>
  <#list authorList as author>
      <li><a href="/g/author/${author.id}">${author.name}</a></li>
  </#list>
</ul>

</@pt.page>
