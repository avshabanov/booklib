<#import "template/page.ftl" as pt/>
<@pt.page title="Book &raquo; ${book.meta.title}">

<h2>${book.meta.title}</h2>

<hr/>

<!-- Information -->
<table class="book-info">
  <tbody>
    <tr>
      <td>ID:</td>
      <td>${book.meta.id?c}</td>
    </tr>
    <tr>
      <td>Authors:</td>
      <td>
        <#list book.authors as author>
          <a href="/g/author/${author.id?c}" title="${author.name?html}">${author.name?html}</a><#if author_has_next>,&nbsp</#if>
        </#list>
      </td>
    </tr>
    <tr>
      <td>Genres:</td>
      <td>
        <#list book.genres as genre>
          <a href="/g/genre/${genre.id?c}" title="${genre.name?html}">${genre.name}</a><#if genre_has_next>,&nbsp</#if>
        </#list>
      </td>
    </tr>
    <tr>
      <td>File Size:</td>
      <td>${book.meta.fileSize?c} byte(s)</td>
    </tr>
    <tr>
      <td>Add Date:</td>
      <td>${book.meta.addDate?html}</td>
    </tr>
    <tr>
      <td>Language:</td>
      <td>${book.meta.lang?html}</td>
    </tr>
    <tr>
      <td>Origin:</td>
      <td>${book.meta.origin?html}</td>
    </tr>
  </tbody>
</table>

<hr/>

<h3><a href="/g/book/${book.meta.id?c}/download">Download&nbsp;<span class="glyphicon glyphicon-download" aria-hidden="true"></span></a></h3>

</@pt.page>
