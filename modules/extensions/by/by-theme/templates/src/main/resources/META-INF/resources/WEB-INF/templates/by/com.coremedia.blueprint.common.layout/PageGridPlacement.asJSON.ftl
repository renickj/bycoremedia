<@cm.responseHeader name="Content-Type" value="application/json; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->
{ "placementName":"${self.name}", "type":"placement", "items" : [
<#list self.items![] as item>
    <@cm.include self=item view="asJSON"/><#if item_has_next>,</#if>
</#list>
]}