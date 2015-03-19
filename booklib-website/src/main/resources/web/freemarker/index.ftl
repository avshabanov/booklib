<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<#import "template/named-value.ftl" as namedValue/>

<@pt.page title="Main">

<h2>Book Library <small>demo</small></h2>

<h3>Favorite Books</h3>

<#if favBooks?has_content>
<ul class="book-list">
  <#list favBooks as bookModel>
    <@books.book model=bookModel />
  </#list>
</ul>
<#else>
<p>You don't have favorite books yet.</p>
</#if>

<h3>Favorite Authors</h3>

<#if favAuthors?has_content>
<@namedValue.list valuePageUrlPrefix="/g/author" listModel=favAuthors />
<#else>
<p>You don't have favorite authors yet.</p>
</#if>

</@pt.page>
