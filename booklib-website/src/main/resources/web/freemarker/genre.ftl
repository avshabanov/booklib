<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<#import "template/pager.ftl" as pager/>
<@pt.page title="Genre &raquo; ${pageModel.namedValue.name?html}">

<h2>${pageModel.namedValue.name?html}</h2>
<p>Bibliography:</p>

<@pager.firstNextBook pageUrl="/g/genre/${pageModel.namedValue.id?c}" curBookId=curBookId!0 startBookId=pageModel.startBookId />

<ul class="book-list">
  <#list pageModel.books as bookModel>
    <@books.book model=bookModel />
  </#list>
</ul>

<@pager.firstNextBook pageUrl="/g/genre/${pageModel.namedValue.id?c}" curBookId=curBookId!0 startBookId=pageModel.startBookId />

</@pt.page>
