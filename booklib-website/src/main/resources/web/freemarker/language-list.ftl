<#import "template/page.ftl" as pt/>
<#import "template/named-value.ftl" as namedValue/>
<@pt.page title="Languages">

<h2>Languages</h2>

<@namedValue.list valuePageUrlPrefix="/g/language" listModel=langList />

</@pt.page>
