<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="js" type="com.coremedia.blueprint.common.contentbeans.CMJavaScript" -->

<#-- include javascript at bottom for performance -->
<#list self.javaScript![] as js>
  <#-- items in result of getJavaScript() are always CMJavaScript -->
  <#if !js.ieExpression?has_content>
    <@cm.include self=js view="asJSLink"/>
  </#if>
</#list>

<#-- hook for extensions at bottom of page (for e.g. javascripts) -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />

<#-- include preview javascript  -->
<#if cm.isPreviewCae()>
  <#assign previewJs=bp.setting(self, "previewJs", []) />
  <#list previewJs as js>
    <@cm.include self=js view="asJSLink"/>
  </#list>
</#if>
