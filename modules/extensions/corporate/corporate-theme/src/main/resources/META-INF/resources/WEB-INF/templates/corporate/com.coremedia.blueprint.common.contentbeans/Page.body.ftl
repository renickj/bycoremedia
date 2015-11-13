<#include "../includes/slider.ftl" />
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
<#-- @ftlvariable name="sliderMetadata" type="java.lang.String" -->

<#assign gridModifier="" />
<#assign superheroPlacement=bp.getPlacementByName("hero", self)!cm.UNDEFINED />
<#if ((superheroPlacement.viewTypeName)?has_content && superheroPlacement.viewTypeName == "superhero" && superheroPlacement.getItems()?size > 0)>
  <#assign gridModifier="cm-grid--with-superhero" />
</#if>

<body id="top" class="cm-grid ${(self.pageGrid.cssClassName)!""} ${gridModifier} container-fluid"<@cm.metadata data=sliderMetadata />>

<#-- skiplinks -->
<div class="cm-skiplinks sr-only sr-only-focusable">
  <ul>
    <li><a href="#cm-navigation"><@bp.message "Skiplinks_Navigation" /></a></li>
    <li><a href="#cm-main"><@bp.message "Skiplinks_Content" /></a></li>
    <li><a href="#cm-search"><@bp.message "Skiplinks_Search" /></a></li>
  </ul>
</div>

<#-- render page grid with content -->
<@cm.include self=self.pageGrid!cm.UNDEFINED />

<#-- info icon for developer mode -->
<#if cmpage.developerMode>
  <div class="cm-preview-developer-mode" data-cm-developer-mode="true" aria-label="Developer Mode">
    <i class="glyphicon glyphicon-wrench" title="You're in Developer Mode" aria-hidden="true"></i>
  </div>
  <#-- this js is used for a automatic reload of webrources changes, triggert by the grunt watch task -->
  <script src="http://localhost:35729/livereload.js"></script>
</#if>

<#-- info box for users with javascript disabled -->
<div class="cm-javascript">
  ${bp.getMessage("Error_NoJavascript")}
</div>

<@cm.include self=self view="bodyEnd"/>

</body>
