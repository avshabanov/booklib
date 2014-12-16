<#import "template/page.ftl" as pt/>
<#import "template/book-li-std.ftl" as bookLiStd/>
<@pt.page title="Main">

<p>Main Page</p>
<p><a href="/g/hello">Hello page</a></p>

<ul>
  <@bookLiStd.book title="J2EE without EJB"/>
  <@bookLiStd.book title="Effective Java"/>
</ul>

</@pt.page>
