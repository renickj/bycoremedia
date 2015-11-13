<#-- @ftlvariable name="self" type="com.coremedia.blueprint.personalization.contentbeans.CMP13NSearch" -->

<#assign fragmentViews={
    "DEFAULT": "Preview_Label_Default",
    "asTeaser": "Preview_Label_Teaser"
  } />
<#assign additionalAttr={} />
<#-- searchStatusAsJSON is only assigned if getItems() has been called -->
<#assign items=self.getItems() />
<#if self.searchStatusAsJSON?has_content>
  <#assign additionalAttr = additionalAttr + {"data-cm-personalization-editorplugin-searchstatus": self.searchStatusAsJSON} />
</#if>

<@cm.include self=self view="multiViewPreview" params={
    "fragmentViews": fragmentViews,
    "additionalAttr": additionalAttr
  }/>
