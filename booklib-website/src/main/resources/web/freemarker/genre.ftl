<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<@pt.page title="Genre &raquo; ${pageModel.namedValue.name?html}">

<h2>${pageModel.namedValue.name?html}</h2>
<p>Bibliography:</p>

<ul class="book-list">
  <#list pageModel.books as bookModel>
    <@books.book model=bookModel />
  </#list>
</ul>

<div>
  <nav>
    <ul class="pager">
      <li><a href="/g/genre/${pageModel.namedValue.id}">&lArr; First Page</a></li>
      <#if pageModel.nextBookId??>
        <li><a href="/g/genre/${pageModel.namedValue.id}?nextBookId=${pageModel.nextBookId}">Next Page &rarr;</a></li>
      </#if>
    </ul>
  </nav>
</div>

</@pt.page>
