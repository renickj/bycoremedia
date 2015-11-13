<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<div class="cm-placement-header__item cm-icon cm-icon--store-locator"<@cm.metadata data=self.content />>
  <@bp.optionalLink href="${cm.getLink(self.target!cm.UNDEFINED)}" attr={
    "title": self.teaserTitle!"",
    "metadata": "properties.title"
  } >
    <i class="cm-icon__symbol icon-store-locator"></i>
    <span class="cm-icon__info cm-visuallyhidden"<@cm.metadata "properties.title" />>${self.teaserTitle!""}</span>
  </@bp.optionalLink>
</div>