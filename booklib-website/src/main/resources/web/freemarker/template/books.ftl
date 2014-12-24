<#macro book model>
<li>
  <div class="container">
    <!-- Title -->
    <div class="row">
      <div class="col-md-12">
        <a href="#"><h3><small>${model.meta.id}</small>&nbsp;${model.meta.title?html}</h3></a>
      </div>
    </div>
    <!-- Authors and Genres -->
    <div class="row">
      <div class="col-md-6">
        <#list model.authors as author>
          <a href="/g/author/${author.id?c}" about="${author.name?html}">${author.name?html}</a><#if author_has_next>,&nbsp</#if>
        </#list>
      </div>
      <div class="col-md-6">
        <#list model.genres as genre>
          <a href="/g/genre/${genre.id?c}" about="${genre.name?html}">${genre.name}</a><#if genre_has_next>,&nbsp</#if>
        </#list>
      </div>
    </div>
  </div>
</li>
</#macro>