<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--picture cm-teaser--hero"<@cm.metadata (metadata![]) + [self.content] />>
  <@cm.include self=self params={
    "limitAspectRatios": lc.getAspectRatiosForTeaserHero(),
    "classBox": "cm-teaser__content cm-aspect-ratio-box",
    "classImage": "cm-aspect-ratio-box__content"
  }/>
  <#if self.teaserTitle?has_content>
    <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
  </#if>
</div>
