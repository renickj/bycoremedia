<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--gallery"<@cm.metadata (metadata![]) + [self.content] />>
  <@bp.optionalLink href="${cm.getLink(self)}">
    <#assign picture=cm.UNDEFINED />
    <#assign metadata=[] />
    <#if self.picture?has_content>
      <#assign picture=self.picture />
      <#assign metadata=["properties.pictures"]/>
    <#elseif self.items?has_content>
      <#assign picture=self.items[0] />
      <#assign metadata=["properties.items"]/>
    </#if>
    <@cm.include self=picture params={
      "limitAspectRatios": lc.getAspectRatiosForTeaser(),
      "classBox": "cm-teaser__content cm-aspect-ratio-box",
      "classImage": "cm-aspect-ratio-box__content",
      "metadata": metadata
    }/>
    <#if self.teaserTitle?has_content>
      <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
    </#if>
  </@bp.optionalLink>
</div>
