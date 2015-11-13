<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED)/>

<div class="cm-placement-header__logo cm-logo"<@cm.metadata data=[self.content, "properties.teaserTitle"] />>
  <@bp.optionalLink href="${link}" attr={
    "title": self.teaserTitle!"",
    "class": "cm-ir"
  }>
    <span class="cm-visuallyhidden">${self.teaserTitle!""}</span>
  </@bp.optionalLink>
</div>
