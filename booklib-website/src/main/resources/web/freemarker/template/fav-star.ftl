<#macro favStar isFavorite>
  <#-- Favorite star icon. -->

  <#if isFavorite>
    <span class="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>
    <#else>
    <span class="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>
  </#if>
</#macro>

