<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<#import "template/pager.ftl" as pager/>
<@pt.page title="Author &raquo; ${pageModel.namedValue.name?html}">

<h2>${pageModel.namedValue.name?html}</h2>
<p>Bibliography:</p>

<@pager.firstNextBook pageUrl="/g/author/${pageModel.namedValue.id?c}" curBookId=curBookId!0 startBookId=pageModel.startBookId!0 />

<ul class="book-list">
  <#list pageModel.books as bookModel>
    <@books.book model=bookModel />
  </#list>
</ul>

<@pager.firstNextBook pageUrl="/g/author/${pageModel.namedValue.id?c}" curBookId=curBookId!0 startBookId=pageModel.startBookId!0 />

</@pt.page>
