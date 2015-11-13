<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#if self?has_content>
  <#list self.rows![] as row>
    <div class="cm-row row">
      <#-- Iterate over each placement-->
      <#list row.placements![] as placement>
        <@cm.include self=placement />
      </#list>
    </div>
  </#list>
</#if>
