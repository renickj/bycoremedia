<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeaser" -->

<#assign fragmentViews={
    "asTeaser": "Preview_Label_Teaser"
  } />
<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentViews
}/>
