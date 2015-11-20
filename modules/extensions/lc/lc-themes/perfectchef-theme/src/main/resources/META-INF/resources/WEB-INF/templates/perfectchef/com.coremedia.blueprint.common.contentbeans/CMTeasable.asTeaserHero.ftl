<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--hero"<@cm.metadata (metadata![]) + [self.content] />>
  <@bp.optionalLink href="${cm.getLink(self.target!cm.UNDEFINED)}">
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": lc.getAspectRatiosForTeaserHero(),
        "classBox": "cm-teaser__content cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content",
        "metadata": ["properties.pictures"]
      }/>
    </#if>
    <#if self.teaserTitle?has_content>
      <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
    </#if>
  </@bp.optionalLink>
</div>