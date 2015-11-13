<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<div id="cm-placement-${self.name!""}" class="cm-grid__item cm-placement-${self.name!""}"<@cm.metadata data=bp.getPlacementPropertyName(self)!""/>>
  <#-- replace main section with the main content to render -->
  <#if self.name! == "main" && cmpage.detailView>
    <@cm.include self=cmpage.content/>
  <#-- render the placement items -->
  <#else>
    <#list self.items![] as item>
      <#-- header -->
      <#if self.name! == "header">
        <@cm.include self=item view="asHeader" />
      <#-- sidebar -->
      <#elseif self.name! == "sidebar">
        <@cm.include self=item view="asTeaser" />
      <#else>
        <@cm.include self=item />
      </#if>
    </#list>
  </#if>
</div>