<#-- @ftlvariable name="self" type="com.coremedia.livecontext.context.CategoryInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--category"<@cm.metadata metadata![] />>
  <@bp.optionalLink href="${cm.getLink(self)}">
    <div class="cm-teaser__content cm-aspect-ratio-box">
        <img class="cm-aspect-ratio-box__content cm-non-adaptive-content" data-cm-non-adaptive-content='{ "overflow": "true"}' src="${(self.category.thumbnailUrl)!""}" alt="${(self.category.name)!""}" />
    </div>
    <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed">${(self.category.name)!""}</h2>
  </@bp.optionalLink>
</div>
