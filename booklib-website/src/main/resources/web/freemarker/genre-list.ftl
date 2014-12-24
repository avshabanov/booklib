<#import "template/page.ftl" as pt/>
<#import "template/named-value.ftl" as namedValue/>
<@pt.page title="Genres">

<h2>Genres</h2>

<@namedValue.list valuePageUrlPrefix="/g/genre" listModel=genreList />

</@pt.page>
