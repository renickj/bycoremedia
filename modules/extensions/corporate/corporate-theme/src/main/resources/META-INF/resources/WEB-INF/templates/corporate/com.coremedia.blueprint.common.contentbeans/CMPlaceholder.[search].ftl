<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage.context" type="com.coremedia.blueprint.common.contentbeans.CMContext" -->
<#assign searchAction=bp.setting(cmpage.context,"searchAction")/>
<@cm.include self=searchAction view="asSearchField" />
