<#-- This template is used at bottom of <body/> if cae.use.local.resources or cae.developer.mode are set to true -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMJavaScript" -->

<#assign link=self.dataUrl/>

<#if !link?has_content>
  <#assign link=cm.getLink(self)/>
</#if>

<script src="${link}"<@cm.metadata self.content />></script>
