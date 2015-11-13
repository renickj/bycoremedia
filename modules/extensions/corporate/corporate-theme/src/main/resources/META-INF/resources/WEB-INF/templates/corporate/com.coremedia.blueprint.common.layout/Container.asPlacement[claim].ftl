<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="isInSidebar" type="java.lang.Boolean" -->

<#assign isInSidebar=cm.localParameter("isInSidebar", false) />

<@cm.include self=self view="asClaim" params={"isInSidebar": isInSidebar} />
