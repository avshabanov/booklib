<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<@pt.page title="Main">

<h2>Book Library <small>demo</small></h2>
<p>Random Books:</p>

<ul class="book-list">
  <#list randomBooks as bookModel>
    <@books.book model=bookModel />
  </#list>
</ul>

</@pt.page>
