<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
{ "id":"${self.contentId}", "type":"collection", "viewTypeName":"${self.viewTypeName!""}", "items" : [
<#list self.items![] as item>
    <@cm.include self=item!cm.UNDEFINED view="asJSON" /><#if item_has_next>,</#if>
</#list>
]}