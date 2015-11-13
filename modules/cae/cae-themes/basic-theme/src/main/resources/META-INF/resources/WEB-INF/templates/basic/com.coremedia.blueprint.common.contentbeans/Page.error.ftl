<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="viewException" type="com.coremedia.objectserver.view.ViewException" -->
<#-- @ftlvariable name="errorHeadline" type="java.lang.String" -->
<#-- @ftlvariable name="errorText" type="java.lang.String" -->

<#assign localeAttr="" />
<#if self.locale?has_content>
  <#assign localeAttr=" lang=\"" + self.locale!"" + "\"" />
</#if>
<#if !errorHeadline?has_content>
  <#assign errorHeadline=bp.getMessage("error.headline") />
</#if>
<#if !errorText?has_content>
  <#assign errorText=bp.getMessage("error.text") />
</#if>
<!DOCTYPE html>
<html${localeAttr} dir="ltr"<@cm.metadata data=bp.getPageMetadata(self)!"" />>
<head>
  <@cm.include self=self view="head"/>
</head>
<body>
  <div style="margin: 20px;">
    <h1>CAE Rendering Error</h1>
    <@bp.notification type="error" title=errorHeadline text=errorText dismissable=false />
    <#if cm.isPreviewCae() && viewException?has_content>
      <@cm.include self=viewException params={"errorIcon": "info", "compact": false} />
    </#if>
  </div>
</body>
</html>