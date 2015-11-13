<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->

<#assign params={"classVideo": classVideo!"", "hideControls": hideControls!false} />

<#if (self.dataUrl!"")?contains("www.kaltura.com")>
  <@cm.include self=self view="kaltura" params=params />
<#elseif (self.dataUrl!"")?contains("youtube.com") || (self.dataUrl!"")?contains("youtu.be")>
  <@cm.include self=self view="youtube" params=params />
<#elseif (self.dataUrl!"")?contains("vimeo.com")>
  <@cm.include self=self view="vimeo" params=params />
<#else>
  <@cm.include self=self view="html5" params=params />
</#if>
