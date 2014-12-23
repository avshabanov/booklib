<#import "template/page.ftl" as pt/>
<@pt.page title="Genres">

<h2>Genres</h2>

<ul>
  <#list genreList as genre>
    <li><a href="/g/genre/${genre.id}">${genre.name}</a></li>
  </#list>
</ul>

</@pt.page>
