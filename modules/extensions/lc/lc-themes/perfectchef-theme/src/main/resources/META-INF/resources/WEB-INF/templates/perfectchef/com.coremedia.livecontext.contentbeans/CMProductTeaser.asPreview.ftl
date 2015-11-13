<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->

<#assign fragmentViews={
    "asTeaserHero": "Preview_Label_Teaser_Hero",
    "asTeaser": "Preview_Label_Teaser"
  } />

<#if self.content.externalId?has_content>
<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentViews
}/>
</#if>