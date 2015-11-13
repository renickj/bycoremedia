<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#include "../includes/slider-fragment.ftl" />
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMObject" -->

<#-- assume that CMObject is instanceof Page, if not provide fallback -->
<#-- @ftlvariable name="page" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#assign page=self />
<#assign language=(page.locale.language)!"en" />
<#assign direction=(page.direction)!"ltr" />
<!DOCTYPE html>
<!--[if lte IE 8]> <html class="no-js lt-ie9" lang="${language}" dir="${direction}"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="${language}" dir="${direction}"> <!--<![endif]-->
<@cm.include self=cmpage view="head"/>

<body id="top" class="cm-page-preview"<@cm.metadata sliderMetadata!"" />>

  <@cm.include self=self view="asPreview"/>

  <#-- Show icon in CoreMedia developerMode -->
  <#if cmpage.developerMode>
    <div class="cm-preview-developer-mode" data-cm-developer-mode="true">
      <i class="icon-wrench" title="You're in Developer Mode"></i>
    </div>
  </#if>

  <@cm.include self=cmpage view="bodyEnd"/>

</body>
</html>
