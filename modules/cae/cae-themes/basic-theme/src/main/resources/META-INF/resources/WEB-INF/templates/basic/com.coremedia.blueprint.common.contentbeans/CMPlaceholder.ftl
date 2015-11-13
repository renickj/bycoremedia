<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<#-- use layout as view -->
<#if (self.viewtype.layout)?has_content>
  <#assign layout=self.viewtype.layout />
  <@cm.include self=self view="[${layout}]" />
<#-- @deprecated: use id, if no layout is set -->
<#elseif self.id?has_content>
  <#assign substitution=bp.substitute(self.id!"", self)!cm.UNDEFINED />
  <@cm.include self=substitution />
<#-- otherwise do nothing. Placeholder without layout or id can't be displayed -->
</#if>
