<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->

<#-- same as CMTeasable but with a link to the blob data and type and size information -->
<div class="cm-teasable cm-teasable--download"<@cm.metadata self.content/>>
  <#-- Title -->
  <h2 class="cm-teasable__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h2>
  <#-- Downloadlink -->
  <@cm.include self=self view="asLink" params={"cssClass":"cm-teasable__download"}/>
  <#-- Text -->
  <#if self.detailText?has_content>
    <div class="cm-teasable__text"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>
  <#-- Extensions -->
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
</div>

<#-- related -->
<#if self.related?has_content>
<div class="cm-related"<@cm.metadata "properties.related"/>>
  <h2>${bp.getMessage("related.label")}</h2>
  <@cm.include self=bp.getContainer(self.related) view="asTeaser" />
</div>
</#if>
