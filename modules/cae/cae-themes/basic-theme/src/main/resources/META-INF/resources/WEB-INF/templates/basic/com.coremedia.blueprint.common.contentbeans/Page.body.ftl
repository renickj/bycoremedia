<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->

<body id="top" class="${(self.pageGrid.cssClassName)!""}">

<#-- Iterate over each row -->
<#if self.pageGrid?has_content>
  <#list self.pageGrid.rows![] as row>
    <div class="row">
    <#-- Iterate over each placement-->
      <#list row.placements![] as placement>
        <@cm.include self=placement/>
      </#list>
    </div>
  </#list>
</#if>

<#if cmpage.developerMode>
  <div class="cm-preview-developer-mode" data-cm-developer-mode="true">
    <span>Development</span>
  </div>
</#if>

<@cm.include self=self view="bodyEnd"/>

</body>
