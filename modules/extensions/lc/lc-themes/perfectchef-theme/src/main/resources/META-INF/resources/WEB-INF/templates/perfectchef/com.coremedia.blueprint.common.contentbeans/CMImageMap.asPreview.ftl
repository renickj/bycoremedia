<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign fragmentViews={
  "DEFAULT": "Preview_Label_Default",
  "asTeaserHero": "Preview_Label_Teaser_Hero",
  "asTeaser": "Preview_Label_Teaser"
} />

<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentViews
}/>
