<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMHTML" -->

<#if self.data?has_content>
  <#escape x as x?html>
    <#noescape><@cm.include self=self.data view="script"/></#noescape>
  </#escape>
</#if>
