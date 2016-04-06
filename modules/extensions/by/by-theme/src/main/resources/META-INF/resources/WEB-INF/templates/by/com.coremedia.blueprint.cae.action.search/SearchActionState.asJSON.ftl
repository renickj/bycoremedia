<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.search.SearchActionState" -->

<#assign searchResultHits=(self.result.hits)![]/>

<#-- results -->
<#if searchResultHits?has_content>
{
"hits":"${self.result.numHits}", "page":"${self.form.pageNum}", "hitsPerPage":"${self.result.searchQuery.limit}", "query":"${self.form.query}",
  "searchResults":[
    <#list searchResultHits as hit>
        <@cm.include self=hit view="asJSON" params={
          "highlightingMap": self.result.highlightingResults,
          "isLast": hit?is_last
        } /><#if !hit?is_last>,</#if>
      </#list>
  ]
}
<#else>
{
"hits":"0", "page":"0", "hitsPerPage":"0", "query":"${self.form.query}",
"searchResults":[]
}
</#if>