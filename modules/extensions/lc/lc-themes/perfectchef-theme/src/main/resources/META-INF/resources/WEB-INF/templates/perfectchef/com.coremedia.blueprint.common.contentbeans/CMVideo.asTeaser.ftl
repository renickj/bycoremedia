<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#assign ownPictureCssClass="" />

<div class="cm-teaser cm-teaser--video" data-cm-teaser--video='{"preview": ".cm-teaser__content", "player": ".cm-teaser--video__video", "play": ".cm-teaser--video__play"}'<@cm.metadata (metadata![]) + [self.content] />>
  <#if self.picture?has_content>
    <#assign ownPictureCssClass="cm-hidden" />
    <@cm.include self=self.picture params={
    "limitAspectRatios": lc.getAspectRatiosForTeaser(),
    "classBox": "cm-teaser__content cm-aspect-ratio-box",
    "classImage": "cm-aspect-ratio-box__content",
    "metadata": ["properties.pictures"]
    }/>
  </#if>
  <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!}</h2>
  <div class="cm-teaser--video__play"></div>
  <div class="cm-teaser--video__video cm-aspect-ratio-box ${ownPictureCssClass}">
    <@cm.include self=self view="video" params={"classVideo": "cm-aspect-ratio-box__content"} />
  </div>
</div>
