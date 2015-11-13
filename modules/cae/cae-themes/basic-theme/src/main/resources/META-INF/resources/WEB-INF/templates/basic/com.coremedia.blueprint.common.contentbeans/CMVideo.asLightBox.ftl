<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->

<div class="cm-lightbox ${classBox}"<@cm.metadata self.content />>
  <#assign ownPictureCssClass="" />

  <div class="cm-teaser cm-teaser--video" data-cm-teaser--video='{"preview": ".cm-teaser__content", "player": ".cm-teaser--video__video", "play": ".cm-teaser--video__play"}'<@cm.metadata (metadata![]) + [self.content] />>
    <#if self.picture?has_content>
      <#assign ownPictureCssClass="cm-hidden" />
      <@cm.include self=self.picture params={
      "limitAspectRatios": limitAspectRatios,
      "classBox": "cm-teaser__content cm-aspect-ratio-box",
      "classImage": "cm-aspect-ratio-box__content",
      "metadata": ["properties.pictures"]
      }/>
    </#if>
    <div class="cm-teaser--video__play"></div>
    <div class="cm-teaser--video__video cm-aspect-ratio-box ${ownPictureCssClass}">
    <@cm.include self=self view="video" params={"classVideo": "cm-aspect-ratio-box__content"} />
    </div>
  </div>

</div>
