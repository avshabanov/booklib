<#import "template/page.ftl" as pt/>
<#import "template/books.ftl" as books/>
<@pt.page title="Main">

<p>Main Page</p>
<p><a href="/g/hello">Hello page</a></p>

<ul>
  <@books.book title="J2EE without EJB"/>
  <@books.book title="Effective Java"/>
</ul>

</@pt.page>
