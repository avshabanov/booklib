<#macro favStar isFavorite itemId itemType>
  <#-- Favorite star icon. -->

  <!-- Fav for ${itemType}/${itemId} -->
  <a class="j-fav-link" href="#">
  <#if isFavorite>
    <span class="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>&nbsp;Unstar
    <#else>
    <span class="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>&nbsp;Star
  </#if>
  </a>
</#macro>

