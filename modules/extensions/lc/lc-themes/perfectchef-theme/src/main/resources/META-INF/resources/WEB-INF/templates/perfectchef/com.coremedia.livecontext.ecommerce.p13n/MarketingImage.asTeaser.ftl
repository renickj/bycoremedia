<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingImage" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--image"<@cm.metadata metadata![] />>
  <div class="cm-teaser__content cm-aspect-ratio-box">
    <img class="cm-aspect-ratio-box__content cm-non-adaptive-content" data-cm-non-adaptive-content='{"overflow": "false"}' src="${(self.thumbnailUrl)!""}" alt="${(self.shortText)!""}" />
  </div>
</div>
