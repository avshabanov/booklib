<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<@pt.page title="Main">

<p>Main Page</p>
<p><a href="/g/hello">Hello page</a></p>

<ul>
  <#list randomBooks as book>
    <@books.book title="${book.meta.title}" authors=book.authors genres=book.genres />
  </#list>
</ul>

</@pt.page>
