<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="js" type="com.coremedia.blueprint.common.contentbeans.CMJavaScript" -->

<#assign context=self.context/>

<#-- include all css (incl. with ieExpressions) -->
<#list self.css![] as css>
  <@cm.include self=css view="asCSSLink" />
</#list>

<#-- include all javascript without ieExpressions -->
<#list self.javaScript![] as js>
  <#if !js.ieExpression?has_content>
    <@cm.include self=js view="asJSLink" />
  </#if>
</#list>
