<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self.content" type="com.coremedia.blueprint.common.contentbeans.CMLinkable" -->
<#-- @ftlvariable name="js" type="com.coremedia.blueprint.common.contentbeans.CMJavaScript" -->

<#assign studioExtraFilesMetadata=cm.getStudioAdditionalFilesMetadata(bp.setting(self, "studioPreviewCss"), bp.setting(self, "studioPreviewJs"))/>
<#assign titleSuffix=bp.setting(self, "customTitleSuffixText", '')/>
<head<@cm.metadata data=studioExtraFilesMetadata/>>
<#-- add encoding first! -->
    <meta charset="UTF-8"/>
<#-- SEO: title -->
    <title<@cm.metadata "properties.htmlTitle" />>${self.content.htmlTitle!"CoreMedia CMS - No Page Title"} ${titleSuffix}</title>
<#-- SEO: description -->
<#if self.content.htmlDescription?has_content>
    <meta name="description" content="${self.content.htmlDescription}" />
</#if>
<#-- SEO: keywords -->
<#if self.content.keywords?has_content>
  <meta name="keywords" content="${self.content.keywords}" />
</#if>
<#-- viewport for responsive design -->
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
<#-- favicon -->
<#if self.favicon?has_content>
    <link rel="shortcut icon" href="${cm.getLink(self.favicon)}"<@cm.metadata "properties.favicon" /> />
</#if>
<#-- SEO: canonical -->
<#if self.content?has_content>
    <#assign currentPageUrl= cm.getLink(self.content)/>
    <link rel="canonical" href="${cm.getLink(self.content)}" />
</#if>
<#-- SEO: i18n -->
<#if (self.content.localizations)?has_content>
  <#assign localizations=self.content.localizations![] />
  <#list localizations as localization>
    <#-- list all localized variants without self -->
    <#if localization.locale != self.content.locale>
        <link rel="alternate" hreflang="${localization.locale.toLanguageTag()}" href="${cm.getLink(localization)}" title="${localization.locale.getDisplayName(self.content.locale)} | ${localization.locale.getDisplayName()}" />
    </#if>
  </#list>
</#if>

<#-- remove no-js class before loading css and more -->
<script>document.documentElement.className = document.documentElement.className.replace(/no-js/g, 'js');</script>

<#--Generate search URL-->
    <script>
        this.searchUrlBase = "/blueprint/servlet/service/jsonsearch/beautifulyou/${bp.setting(cmpage, "searchAction", false).contentId}";
        this.suggestUrlBase  = this.searchUrlBase +"?rootNavigationId=${self.context.rootNavigations[0].contentId}";
    </script>

<#-- include css -->
<#list self.css![] as css>
    <@cm.include self=css view="asCSSLink"/>
</#list>

<#-- include preview css -->
<#if cm.isPreviewCae()>
  <#assign previewCss=bp.setting(self, "previewCss", []) />
  <#list previewCss as css>
    <@cm.include self=css view="asCSSLink"/>
  </#list>
</#if>

<#-- include javascript with ieExpressions in head, all others in footer -->
<#list self.javaScript![] as js>
  <#if js.ieExpression?has_content>
    <@cm.include self=js view="asHeaderJSLink"/>
  </#if>
</#list>

<#-- include pbe -->
<@cm.previewScripts/>

<#-- hook for extensions in head (for e.g. css or javascripts) -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_HEAD />

</head>
