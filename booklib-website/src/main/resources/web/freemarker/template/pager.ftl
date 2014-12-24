<#macro firstNextBook pageUrl curBookId startBookId>
  <#-- Pager for book lists. Zero value is used as a sentinel. -->

  <div>
    <nav>
      <ul class="pager">
        <#if curBookId gt 0>
          <li><a href="${pageUrl}" title="Go to the very first page">&lArr; First Page</a></li>
        </#if>
        <#if startBookId gt 0>
          <li><a href="${pageUrl}?startBookId=${startBookId?c}" title="Go to the next page"><strong>Next Page</strong> &rarr;</a></li>
        </#if>
      </ul>
    </nav>
  </div>

</#macro>

