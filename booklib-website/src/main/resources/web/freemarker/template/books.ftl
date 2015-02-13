<#macro book model>
<#import "fav-star.ftl" as fs/>
<li>
  <div class="container">
    <!-- Title -->
    <div class="row">
      <div class="col-md-1">
        <@fs.favStar isFavorite=model.favorite/>
      </div>
      <div class="col-md-11">
        <a href="/g/book/${model.meta.id?c}" title="${model.meta.title?html}"><h3><small>${model.meta.id?c}</small>&nbsp;${model.meta.title?html}</h3></a>
      </div>
    </div>
    <!-- Authors and Genres -->
    <div class="row">
      <div class="col-md-6">
        <#list model.authors as author>
          <a href="/g/author/${author.id?c}" title="${author.name?html}">${author.name?html}</a><#if author_has_next>,&nbsp</#if>
        </#list>
      </div>
      <div class="col-md-6">
        <#list model.genres as genre>
          <a href="/g/genre/${genre.id?c}" title="${genre.name?html}">${genre.name}</a><#if genre_has_next>,&nbsp</#if>
        </#list>
      </div>
    </div>
  </div>
</li>
</#macro>
