<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.search.SearchActionState" -->

<#assign searchResultHits=(self.result.hits)![]/>




<#-- results -->
<#if searchResultHits?has_content>
  [
    <#list searchResultHits as hit>
        <@cm.include self=hit view="asJSON" params={
          "highlightingMap": self.result.highlightingResults,
          "isLast": hit?is_last
        } />
      </#list>

  ]
</#if>