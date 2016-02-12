<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#list self.items![] as item>
  <@cm.include self=item />
</#list>
