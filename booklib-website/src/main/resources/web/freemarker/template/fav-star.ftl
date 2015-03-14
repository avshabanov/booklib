<#macro favStar isFavorite itemId itemType>
  <#-- Favorite star icon. -->

  <a class="j-fav-link <#if isFavorite>fav</#if>" href="#" item-id="${itemId?c}" item-type="${itemType}">
    <span class="star"><span class="glyphicon glyphicon glyphicon-star" aria-hidden="true"></span>&nbsp;Unstar</span>
    <span class="unstar"><span class="glyphicon glyphicon glyphicon-star-empty" aria-hidden="true"></span>&nbsp;Star</span>
  </a>
</#macro>

