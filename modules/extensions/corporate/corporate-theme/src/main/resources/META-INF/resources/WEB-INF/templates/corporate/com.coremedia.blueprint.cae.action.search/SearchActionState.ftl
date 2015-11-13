<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.search.SearchActionState" -->

<#assign searchResultHits=(self.result.hits)![]/>

<div class="cm-search cm-search--results">

  <div class="cm-search__header">
    <#-- headline -->
    <h1 class="cm-search__headline">${bp.getMessage("search.form.results")}</h1>
    <#-- box with infos about this search -->
    <#if self.queryTooShort!false>
      <div class="alert alert-warning" role="alert">
        ${bp.getMessage("search.error.belowMinQueryLength")}
      </div>
    <#elseif (searchResultHits?size == 0)>
      <div class="alert alert-warning" role="alert">
        <@cm.unescape bp.getMessage("search.error.noresults", [self.form.query!""])/>
      </div>
    <#else>
      <div class="alert alert-info" role="alert">
        <@cm.unescape bp.getMessage("search.searchTerm", [self.result.numHits, self.form.query!""])/>
      </div>
    </#if>
  </div>

  <#-- results -->
  <#if searchResultHits?has_content>
    <div class="cm-search__results">
      <#list searchResultHits as hit>
        <@cm.include self=hit view="asSearchResult" params={
          "highlightingMap": self.result.highlightingResults,
          "isLast": hit?is_last
        } />
      </#list>
    </div>
  </#if>
</div>
