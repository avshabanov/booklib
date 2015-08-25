<#import "template/page.ftl" as pt/>
<#import "template/named-value.ftl" as namedValue/>
<@pt.page title="Authors">

<#--
    Provides a list of authors
-->

<h2>Authors</h2>

<@namedValue.list valuePageUrlPrefix="/g/author" listModel=authorList />

</@pt.page>
